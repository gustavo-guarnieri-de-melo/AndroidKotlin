package br.com.alura.orgs.ui.activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityLoginBinding
import br.com.alura.orgs.extensions.toast
import br.com.alura.orgs.extensions.vaiPara
import br.com.alura.orgs.model.Usuario
import br.com.alura.orgs.preferences.dataStore
import br.com.alura.orgs.preferences.usuarioLogadoPreferences
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val usuarioDao by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraBotaoCadastrar()
        configuraBotaoEntrar()
        configuraBotaoTogglePasswordVisibility()
    }
    private fun configuraBotaoTogglePasswordVisibility() {
        val senhaEditText = binding.activityLoginSenha
        val toggleButton = findViewById<ImageButton>(R.id.buttonTogglePasswordVisibility)

        toggleButton.setOnClickListener {
            if (senhaEditText.transformationMethod == PasswordTransformationMethod.getInstance()) {
                // Se a senha estiver oculta, mostre-a
                senhaEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                toggleButton.setImageResource(R.drawable.baseline_remove_red_eye_24)
            } else {
                // Se a senha estiver visível, oculte-a
                senhaEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                toggleButton.setImageResource(R.drawable.baseline_remove_red_eye_24)
            }
        }
    }

    private fun configuraBotaoEntrar() {
        binding.activityLoginBotaoEntrar.setOnClickListener {
            val usuario = binding.activityLoginUsuario.text.toString()
            val senha = binding.activityLoginSenha.text.toString()
            Log.i("LoginActivity", "onCreate: $usuario - $senha")
            autentica(usuario, senha)

        }
    }

    private fun autentica(usuario: String, senha: String) {
        lifecycleScope.launch {
            usuarioDao.autentica(usuario, senha)?.let { usuario ->
                dataStore(usuario)
                vaiPara(ListaProdutosActivity::class.java)
            } ?: toast("Falha na autenticação")
        }
    }

    private suspend fun dataStore(usuario: Usuario) {
        dataStore.edit { preferences ->
            preferences[usuarioLogadoPreferences] = usuario.id
        }
    }

    private fun configuraBotaoCadastrar() {
        binding.activityLoginBotaoCadastrar.setOnClickListener {
            vaiPara(FormularioCadastroUsuarioActivity::class.java)
        }
    }

}