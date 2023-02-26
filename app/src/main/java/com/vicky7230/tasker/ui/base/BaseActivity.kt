package com.vicky7230.tasker.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.utils.findGenericWithType
import javax.inject.Inject


abstract class BaseActivity<viewDataBinding:ViewDataBinding> : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var binding: viewDataBinding


    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindingClass =
            javaClass.findGenericWithType<viewDataBinding>(ViewDataBinding::class.java)
        binding = bindingClass?.getMethod("inflate", LayoutInflater::class.java)
            ?.invoke(null, LayoutInflater.from(this)) as viewDataBinding
        setContentView(binding.root)
        binding.lifecycleOwner = this
        onBindingCreated()
        registerObservers()
    }

    open fun onBindingCreated() {}

    operator fun <T> LiveData<T>.invoke(observer: ((T) -> Unit)) {
        observe(this@BaseActivity) { observer(it) }
    }

    open fun registerObservers() {}
}