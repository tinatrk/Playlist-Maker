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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.app.App.Companion.EXTERNAL_STORAGE_NAME
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.playlists.presentation.models.CreatePlaylistsScreenState
import com.example.playlistmaker.playlists.presentation.models.PlaylistCreationState
import com.example.playlistmaker.playlists.presentation.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {

    private val viewModel: CreatePlaylistViewModel by viewModel()

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
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storagePath = getStoragePath()

        backDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
            .setTitle(requireContext().getString(R.string.back_title))
            .setMessage(requireContext().getString(R.string.back_message))
            .setNeutralButton(requireContext().getString(R.string.back_cancel)) { dialog, which -> }
            .setPositiveButton(requireContext().getString(R.string.back_ok)) { dialog, which ->
                parentFragmentManager.popBackStack()
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        binding.toolbarCreatePlaylistScreen.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        permissionDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
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

                binding.btnCreatePlaylist.isEnabled = !s.isNullOrEmpty()
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

        binding.btnCreatePlaylist.setOnClickListener {
            viewModel.createPlaylist(
                binding.etPlaylistTitle.text.toString(),
                binding.etPlaylistDescription.text.toString(),
                storagePath
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                renderState(state)
            }
        }

        viewModel.observeOnCreateClickedLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistCreationState.SuccessCreated -> {
                    saveCoverToStorage(state.coverUri, state.filePath)
                    Snackbar.make(
                        binding.btnCreatePlaylist,
                        "${requireActivity().getString(R.string.toast_playlist)} " +
                                "${state.playlistTitle} ${requireActivity().getString(R.string.toast_created)}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    parentFragmentManager.popBackStack()
                }

                is PlaylistCreationState.AlreadyExists -> {
                    Snackbar.make(
                        binding.btnCreatePlaylist,
                        "${requireActivity().getString(R.string.toast_playlist)} " +
                                "${state.playlistTitle} ${requireActivity().getString(R.string.toast_already_exists)}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }

        viewModel.observeOnBackClickedLiveData().observe(viewLifecycleOwner) { isContentEntered ->
            if (isContentEntered) backDialog.show() else parentFragmentManager.popBackStack()
        }
    }

    private fun renderState(state: CreatePlaylistsScreenState) {
        when (state) {
            is CreatePlaylistsScreenState.Empty -> showEmptyScreen()
            is CreatePlaylistsScreenState.Content -> showContent(state.coverUri)
        }
    }

    private fun showEmptyScreen() {
        binding.createPlaylistScreen.isEnabled = false
    }

    private fun showContent(uri: String) {
        val cornerRadiusDp = (requireActivity().resources
            .getDimension(R.dimen.corner_radius_8)).toInt()
        Glide.with(requireContext())
            .load(uri)
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

    private fun getStoragePath(): File {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            EXTERNAL_STORAGE_NAME
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        return filePath
    }

    private fun saveCoverToStorage(coverUri: String, targetFile: File?) {
        if (coverUri == EMPTY_STRING || targetFile == null) return

        val inputStream = requireContext().contentResolver.openInputStream(coverUri.toUri())
        val outputStream = FileOutputStream(targetFile)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    companion object {
        private const val EDIT_TEXT_TITLE = 1
        private const val EDIT_TEXT_DESCRIPTION = 2
        private const val SCHEME = "package"

        fun newInstance(): CreatePlaylistFragment = CreatePlaylistFragment()
    }
}