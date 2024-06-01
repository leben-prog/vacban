// MainActivity.kt
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.example.vacban.AdminActivity
import com.example.vacban.R
import com.example.vacban.RegActivity
import com.example.vacban.UserActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userPassword: EditText = findViewById(R.id.user_password)
        val loginButton: Button = findViewById(R.id.button_log)
        val registerButton: Button = findViewById(R.id.button_reg)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPassword.text.toString().trim()

            if (login == "" || pass == "") {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else {
                val db =    DbHelper(this)
                val isAdmin = db.isAdmin(login, pass)

                if (isAdmin) {
                    // Если пользователь является администратором
                    Toast.makeText(this, "Администратор $login авторизован", Toast.LENGTH_LONG).show()
                    // Очищаем поля ввода
                    userLogin.text.clear()
                    userPassword.text.clear()
                    // Переходим на AdminActivity
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                } else {
                    // Если пользователь не является администратором, продолжаем аутентификацию
                    val isAuth = db.getUser(login, pass)
                    if (isAuth) {
                        // Если аутентификация успешна
                        Toast.makeText(this, "Пользователь $login авторизован", Toast.LENGTH_LONG).show()
                        // Очищаем поля ввода
                        userLogin.text.clear()
                        userPassword.text.clear()
                        // Переходим на UserActivity
                        val intent = Intent(this, UserActivity::class.java)
                        intent.putExtra("userId", login.hashCode())
                        startActivity(intent)
                    } else {
                        // Если аутентификация не успешна
                        Toast.makeText(this, "Пользователь $login не авторизован", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
