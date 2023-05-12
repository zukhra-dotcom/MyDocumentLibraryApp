package com.example.mydocumentlibrary.categories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydocumentlibrary.R;


public class SecretsPasscodeActivity extends AppCompatActivity{
//    private TextView enteringLocked;
//    private EditText password;
//    private Button enter;
//    private TextView attempts;
//    private TextView numberOfAttempts;
//
//    //number of attempts to enter
//    int numberOfRemainingLoginAttempts = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secrets_passcode);

//        // Связываемся с элементами нашего интерфейса:
//        password = (EditText) findViewById(R.id.edit_password);
//        enter = (Button) findViewById(R.id.button_enter);
//        enteringLocked = (TextView) findViewById(R.id.entering_locked);
//        attempts = (TextView) findViewById(R.id.attempts);
//        numberOfAttempts = (TextView) findViewById(R.id.number_of_attempts);
//        numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));
//    }
//
//    // Обрабатываем нажатие кнопки "Войти":
//    public void Login(View view) {
//
//        // Если введенные логин и пароль будут словом "secret",
//        // показываем Toast сообщение об успешном входе:
//        if (password.getText().toString().equals("secret")) {
//            Toast.makeText(getApplicationContext(), "Successfully entered",Toast.LENGTH_SHORT).show();
//
//            // Выполняем переход на другой экран:
//            Intent intent = new Intent(SecretsPasscodeActivity.this, SecretsPage.class);
//            startActivity(intent);
//        }
//
//        // В другом случае выдаем сообщение с ошибкой:
//        else {
//            Toast.makeText(getApplicationContext(), "Incorrect credentials!",Toast.LENGTH_SHORT).show();
//            numberOfRemainingLoginAttempts--;
//
//            // Делаем видимыми текстовые поля, указывающие на количество оставшихся попыток:
//            attempts.setVisibility(View.VISIBLE);
//            numberOfAttempts.setVisibility(View.VISIBLE);
//            numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));
//            // Когда выполнено 3 безуспешных попытки залогиниться,
//            // делаем видимым текстовое поле с надписью, что все пропало и выставляем
//            // кнопке настройку невозможности нажатия setEnabled(false):
//            if (numberOfRemainingLoginAttempts == 0) {
//                enter.setEnabled(false);
//                enteringLocked.setVisibility(View.VISIBLE);
//                enteringLocked.setBackgroundColor(Color.RED);
//                enteringLocked.setText("Entering blocked!!!");
//            }
//        }
    }
}






//public class SecretsPasscodeActivity extends AppCompatActivity {

//    PasscodeView passcodeView;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_secrets_passcode);
//
//        passcodeView = findViewById(R.id.passcodeview);
//        passcodeView.setPasscodeLength(5)
//                .setLocalPasscode("12345")
//                .setListener(new PasscodeView.PasscodeViewListener() {
//                    @Override
//                    public void onFail() {
//                        Toast.makeText(SecretsPasscodeActivity.this, "Password is wrong!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSuccess(String number) {
//                        Intent intent_passcode = new Intent(SecretsPasscodeActivity.this, SecretsPage.class);
//                        startActivity(intent_passcode);
//                    }
//                });
//    }
//}