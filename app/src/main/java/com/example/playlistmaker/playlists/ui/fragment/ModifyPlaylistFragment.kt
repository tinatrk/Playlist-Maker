package com.example.playlistmaker.playlists.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.app.App.Companion.EXTERNAL_STORAGE_NAME
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.databinding.FragmentModifyPlaylistBinding
import com.example.playlistmaker.playlists.presentation.models.ModifyPlaylistScreenState
import com.example.playlistmaker.playlists.presentation.models.PlaylistCoverModificationType
import com.example.playlistmaker.playlists.presentation.models.PlaylistModificationState
import com.example.playlistmaker.playlists.presentation.view_model.ModifyPlaylistViewModel
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

open class ModifyPlaylistFragment : BindingFragment<FragmentModifyPlaylistBinding>() {

    private val playlistId: Int by lazy {
        requireArguments().getInt(PLAYLIST_ID_KEY)
    }

    private val viewModel: ModifyPlaylistViewModel by lazy {
        getViewModel { parametersOf(playlistId) }
    }

    private lateinit var backDialog: MaterialAlertDialogBuilder

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.backClicked(
                binding.etPlaylistTitle.text.toString(),
                binding.etPlaylistDescription.text.toString()
            )
        }
    }

    private lateinit var permissionDialog: MaterialAlertDialogBuilder

    private val requester = PermissionRequester.instance()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setCover(uri.toString())
            }
        }

    private lateinit var storagePath: File

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentModifyPlaylistBinding {
        return FragmentModifyPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storagePath = getAppStoragePath()

        backDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.back_title))
            .setMessage(requireContext().getString(R.string.back_message))
            .setNeutralButton(requireContext().getString(R.string.back_cancel)) { dialog, which -> }
            .setPositiveButton(requireContext().getString(R.string.back_ok)) { dialog, which ->
                parentFragmentManager.popBackStack()
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        permissionDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.permission_title))
            .setMessage(requireContext().getString(R.string.permission_message))
            .setNeutralButton(requireContext().getString(R.string.permission_cancel)) { dialog, which -> }
            .setPositiveButton(requireContext().getString(R.string.permission_ok)) { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data =
                    Uri.fromParts(SCHEME, requireContext().packageName, null)
                requireContext().startActivity(intent)
            }

        binding.ivAddCover.setOnClickListener {
            openExternalStorage()
        }

        val titleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBoxStrokeColor(s, EDIT_TEXT_TITLE)

                setHitTextColor(s, EDIT_TEXT_TITLE)

                binding.btnModifyPlaylist.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etPlaylistTitle.addTextChangedListener(titleTextWatcher)

        val descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBoxStrokeColor(s, EDIT_TEXT_DESCRIPTION)

                setHitTextColor(s, EDIT_TEXT_DESCRIPTION)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etPlaylistDescription.addTextChangedListener(descriptionTextWatcher)

        if (playlistId == UNKNOWN_ID) setUniqueCreatePlaylistScreenSettings()
        else setUniqueEditPlaylistScreenSettings()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                renderState(state)
            }
        }

        viewModel.observeOnCreateClickedLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistModificationState.SuccessCreated -> {
                    saveCoverToStorage(state.coverModification)

                    Snackbar.make(
                        binding.btnModifyPlaylist,
                        "${requireActivity().getString(R.string.toast_playlist)} " +
                                "${state.playlistTitle} ${requireActivity().getString(R.string.toast_created)}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    parentFragmentManager.popBackStack()
                }

                is PlaylistModificationState.AlreadyExists -> {
                    Snackbar.make(
                        binding.btnModifyPlaylist,
                        "${requireActivity().getString(R.string.toast_playlist)} " +
                                "${state.playlistTitle} ${requireActivity().getString(R.string.toast_already_exists)}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                is PlaylistModificationState.SuccessUpdated -> {
                    saveCoverToStorage(state.coverModification)
                    findNavController().navigateUp()
                }

                is PlaylistModificationState.NothingChanged -> Snackbar.make(
                    binding.btnModifyPlaylist,
                    requireActivity().getString(R.string.toast_playlist_without_changes),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }

        viewModel.observeOnBackClickedLiveData().observe(viewLifecycleOwner) { isContentEntered ->
            if (isContentEntered) backDialog.show() else parentFragmentManager.popBackStack()
        }
    }

    private fun setUniqueCreatePlaylistScreenSettings() {
        binding.toolbarModifyPlaylistScreen.title =
            requireActivity().getString(R.string.create_playlist_screen_title)
        binding.btnModifyPlaylist.text = requireActivity().getString(R.string.btn_create_playlist)

        binding.toolbarModifyPlaylistScreen.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        backCallback.isEnabled = true

        binding.btnModifyPlaylist.setOnClickListener {
            val newCoverPath = File(storagePath, binding.etPlaylistTitle.text.toString()).toString()
            viewModel.createPlaylist(
                binding.etPlaylistTitle.text.toString(),
                binding.etPlaylistDescription.text.toString(),
                newCoverPath
            )
        }
    }

    private fun setUniqueEditPlaylistScreenSettings() {
        binding.toolbarModifyPlaylistScreen.title =
            requireActivity().getString(R.string.edit_playlist_screen_title)
        binding.btnModifyPlaylist.text = requireActivity().getString(R.string.btn_save_playlist)

        binding.toolbarModifyPlaylistScreen.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        backCallback.isEnabled = false

        binding.btnModifyPlaylist.setOnClickListener {
            val newCoverPath = File(storagePath, binding.etPlaylistTitle.text.toString()).toString()
            viewModel.updatePlaylist(
                binding.etPlaylistTitle.text.toString(),
                binding.etPlaylistDescription.text.toString(),
                newCoverPath
            )
        }
    }

    private fun renderState(state: ModifyPlaylistScreenState) {
        when (state) {
            is ModifyPlaylistScreenState.Init -> {
                setCoverContent(state.coverUri)
                setEditTextContent(state.title, state.description)
            }

            is ModifyPlaylistScreenState.CoverContent -> setCoverContent(state.coverUri)
        }
    }

    private fun setEditTextContent(playlistTitle: String, playlistDescription: String) {
        binding.etPlaylistTitle.setText(playlistTitle)
        binding.etPlaylistDescription.setText(playlistDescription)
        binding.modifyPlaylistScreen.isEnabled = playlistTitle != EMPTY_STRING
    }

    private fun setCoverContent(uri: String) {
        val coverSource = try {
            val inputStream = File(uri).inputStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            uri
        }

        val cornerRadiusDp = (requireActivity().resources
            .getDimension(R.dimen.corner_radius_8)).toInt()
        Glide.with(requireContext())
            .load(coverSource)
            .placeholder(R.drawable.create_playlist_cover)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(cornerRadiusDp)))
            .into(binding.ivAddCover)
    }

    private fun openExternalStorage() {
        viewLifecycleOwner.lifecycleScope.launch {
            requester.request(getManifestPermission())
                .collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }

                        is PermissionResult.Denied.DeniedPermanently -> {
                            permissionDialog.show()
                        }

                        is PermissionResult.Denied, PermissionResult.Cancelled -> {}

                    }
                }
        }
    }

    private fun getManifestPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun setBoxStrokeColor(s: CharSequence?, typeEditText: Int) {
        var editText: TextInputLayout? = when (typeEditText) {
            EDIT_TEXT_TITLE -> binding.tilPlaylistTitle
            EDIT_TEXT_DESCRIPTION -> binding.tilPlaylistDescription
            else -> return
        }

        if (!s.isNullOrEmpty()) {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.box_color_not_empty_text
            )
                ?.let { editText?.setBoxStrokeColorStateList(it) }
        } else {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.box_color_empty_text
            )
                ?.let { editText?.setBoxStrokeColorStateList(it) }
        }
        editText = null
    }

    private fun setHitTextColor(s: CharSequence?, typeEditText: Int) {
        var editText: TextInputLayout? = when (typeEditText) {
            EDIT_TEXT_TITLE -> binding.tilPlaylistTitle
            EDIT_TEXT_DESCRIPTION -> binding.tilPlaylistDescription
            else -> return
        }

        if (!s.isNullOrEmpty()) {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.hint_color_not_empty_text
            )
                ?.let { editText?.defaultHintTextColor = it }
        } else {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.hint_color_empty_text
            )
                ?.let { editText?.defaultHintTextColor = it }
        }

        editText = null
    }

    private fun getAppStoragePath(): File {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            EXTERNAL_STORAGE_NAME
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        return filePath
    }

    private fun saveCoverToStorage(coverInfo: PlaylistCoverModificationType) {
        when (coverInfo) {
            is PlaylistCoverModificationType.NotChanged -> return

            is PlaylistCoverModificationType.Renamed -> {
                renameCoverFile(coverInfo.oldName, coverInfo.newName)
            }

            is PlaylistCoverModificationType.ChangedContent -> {
                overwriteCoverFile(coverInfo.coverName, coverInfo.newContentUri)
            }

            is PlaylistCoverModificationType.RenamedAndChangedContent -> {
                overwriteCoverFile(coverInfo.oldName, coverInfo.newContentUri)
                renameCoverFile(coverInfo.oldName, coverInfo.newName)
            }
        }
    }

    private fun renameCoverFile(oldName: String, newName: String) {
        val coverFile = File(storagePath, oldName)
        val isSuccess = coverFile.renameTo(File(storagePath, newName))
    }

    private fun overwriteCoverFile(coverName: String, newContentUri: String) {
        val inputStream = requireContext().contentResolver.openInputStream(newContentUri.toUri())
        val outputStream = FileOutputStream(File(storagePath, coverName))
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        inputStream?.close()
        outputStream.close()
    }

    companion object {
        private const val EDIT_TEXT_TITLE = 1
        private const val EDIT_TEXT_DESCRIPTION = 2
        private const val SCHEME = "package"
        private const val PLAYLIST_ID_KEY = "playlist_id_key"

        fun newInstance(playlistId: Int): ModifyPlaylistFragment = ModifyPlaylistFragment().apply {
            arguments = bundleOf(PLAYLIST_ID_KEY to playlistId)
        }

        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ID_KEY to playlistId)
    }
}