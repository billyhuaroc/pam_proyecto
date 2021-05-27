package com.example.pam_proyecto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText tvt_correo, tvt_contraseña;
    Button Ingresar;
    FragmentTransaction transaction;
    Fragment fragmentInicio, registrarFragment, salasFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvt_correo = findViewById(R.id.tvt_correo);
        tvt_contraseña = findViewById(R.id.tvt_contraseña);
        fragmentInicio = new InicioFragment();
        registrarFragment = new RegistrarFragment();
        salasFragment = new SalasFragment();

        //invocar nuestro fragmento principal
        //getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments,fragmentInicio).commit();

    }
    public void Ingresar(View v){
        /*transaction = getSupportFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.btnNuevo: transaction.replace(R.id.contenedorFragments,registrarFragment);
                transaction.addToBackStack(null);
                break;

            case R.id.btnIngresar:transaction.replace(R.id.contenedorFragments,salasFragment);
                transaction.addToBackStack(null);
                break;


        }
        transaction.commit();*/
    }
}