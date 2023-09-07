package com.example.flashcards.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.flashcards.R
import com.example.flashcards.databinding.FragmentPracticeBinding
import com.example.flashcards.helpers.SystemHelper
import com.example.flashcards.helpers.SystemHelper.dp
import com.example.flashcards.viewmodels.Finished
import com.example.flashcards.viewmodels.InProgress
import com.example.flashcards.viewmodels.ProgressViewModel
import com.example.flashcards.views.Dialog

class PracticeFragment : Fragment() {
    private val viewModel by activityViewModels<ProgressViewModel>()

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val currStat = viewModel.status.value
            if (currStat is InProgress && currStat.end > 0) {
                showConfirmLeaveDialog()
            } else {
                viewModel.finish()
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressed)

        binding.decorCard.flipEnabled = false

        binding.back.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                is InProgress -> updateCardDisplay(status)
                Finished -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCardDisplay(progress: InProgress) {
        binding.prev.setOnClickListener { viewModel.prev() }
        binding.prev.visibility = if (progress.curr == 0) View.INVISIBLE else View.VISIBLE

        binding.next.setOnClickListener { viewModel.next() }
        binding.next.visibility = if (progress.curr == progress.end) View.INVISIBLE else View.VISIBLE

        progress.prev?.let { prev ->
            shuffle(
                toBack = progress.curr > prev,
                onStart = { populateCard(progress) },
                onEnd = { populateDecorCard(progress) }
            )
        } ?: run {
            populateCard(progress)
            populateDecorCard(progress)
        }

        binding.decorCard.onFlip = null

        binding.progress.text = "${progress.curr + 1} / ${progress.size}"

        binding.sad.setOnClickListener {
            viewModel.next(false)
            if (progress.curr == progress.size - 1) {
                slideOut {
                    findNavController().navigate(R.id.action_practiceFragment_to_finishFragment)
                }
            }
        }

        binding.happy.setOnClickListener {
            viewModel.next(true)
            if (progress.curr == progress.size - 1) {
                slideOut {
                    findNavController().navigate(R.id.action_practiceFragment_to_finishFragment)
                }
            }
        }
    }

    private fun populateCard(progress: InProgress) {
        with(binding.card) {
            front = progress.card.front
            back = progress.card.back
            visibleSide = progress.card.visibleSide
            onFlip = { visibleSide ->
                binding.decorCard.visibleSide = visibleSide
                viewModel.setVisibleSide(progress.curr, visibleSide)
            }
        }
    }

    private fun populateDecorCard(progress: InProgress) {
        with(binding.decorCard) {
            front = progress.card.front
            back = progress.card.back
            visibleSide = progress.card.visibleSide
        }
    }

    private fun showConfirmLeaveDialog() {
        Dialog(context).run {
            setTitle("Leave Practice")
            setMessage("Are you sure you want to leave? Your progress will be lost.")
            setPositiveButton("Leave") { dialog, which ->
                viewModel.finish()
                findNavController().popBackStack()
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            show()
        }
    }

    private fun shuffle(toBack: Boolean, onStart: () -> Unit, onEnd: () -> Unit) {
        val shuffler = ShuffleAnimator.createFor(if (toBack) binding.decorCard else binding.card)
        val sound = MediaPlayer.create(context, R.raw.card_flip)

        shuffler.shuffleOut.doOnEnd {
            binding.card.translationZ = 1f
            binding.decorCard.translationZ = 0f
            shuffler.shuffleIn.start()
        }

        shuffler.shuffleIn.doOnEnd {
            binding.decorCard.visibility = View.INVISIBLE
            binding.card.flipEnabled = true
            onEnd()
        }

        binding.card.flipEnabled = false
        binding.card.translationZ = 0f
        binding.decorCard.translationZ = 1f
        binding.decorCard.visibility = View.VISIBLE
        onStart()
        sound.start()
        shuffler.shuffleOut.start()
    }

    private fun slideOut(onEnd: () -> Unit) {
        val rotate = ObjectAnimator.ofFloat(binding.card, "rotation", 0f, 10f).setDuration(260)
        val translateX = ObjectAnimator.ofFloat(binding.card, "translationX", 0f, SystemHelper.screenSize.first * 1.4f).setDuration(260)
        val translateY = ObjectAnimator.ofFloat(binding.card, "translationY", 0f, 10f).setDuration(100)
        AnimatorSet().apply {
            playTogether(rotate, translateX, translateY)
            doOnEnd {
                binding.card.flipEnabled = true
                onEnd()
            }
            binding.card.flipEnabled = false
            MediaPlayer.create(context, R.raw.card_slide).start()
            start()
        }
    }
}

private class ShuffleAnimator private constructor(
    val shuffleOut: AnimatorSet,
    val shuffleIn: AnimatorSet
) {
    companion object {
        fun createFor(view: View): ShuffleAnimator {
            val xOffsetDp = (view.width / 4f).dp
            val yOffsetDp = (view.height * -0.37f).dp

            val rotateOut = ObjectAnimator.ofFloat(view, "rotation", 0f, 25f).setDuration(250)
            val rotateIn = ObjectAnimator.ofFloat(view, "rotation", 25f, 0f).setDuration(250)
            val xOut = ObjectAnimator.ofFloat(view, "translationX", 0f, xOffsetDp).setDuration(250)
            val xIn = ObjectAnimator.ofFloat(view, "translationX", xOffsetDp, 0f).setDuration(250)
            val yOut = ObjectAnimator.ofFloat(view, "translationY", 0f, yOffsetDp).setDuration(250)
            val yIn = ObjectAnimator.ofFloat(view, "translationY", yOffsetDp, 0f).setDuration(250)

            return ShuffleAnimator(
                AnimatorSet().apply { playTogether(rotateOut, xOut, yOut) },
                AnimatorSet().apply { playTogether(rotateIn, xIn, yIn) }
            )
        }
    }
}
