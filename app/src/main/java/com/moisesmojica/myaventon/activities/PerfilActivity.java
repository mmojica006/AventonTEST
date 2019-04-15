package com.moisesmojica.myaventon.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.moisesmojica.myaventon.R;
import com.moisesmojica.myaventon.api.Api;
import com.moisesmojica.myaventon.api.RequestHandler;
import com.moisesmojica.myaventon.helper.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PerfilActivity extends MainActivity {
    private TextView editTextNombre, editTextApeellido, editTextEmail, editTextCelular;
    private ImageView imageViewPefil;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_perfil, null, false);
        drawerLayout.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.menu_perfil);

        bindUI();
    }

    private void bindUI() {
        editTextNombre = findViewById(R.id.textViewPerfilNombre);
        editTextApeellido = findViewById(R.id.textViewPerfilApellido);
        editTextEmail = findViewById(R.id.textViewPerfilCorreo);
        editTextCelular =  findViewById(R.id.textViewPerfilCelular);
        imageViewPefil = findViewById(R.id.imageViewPerfilPicture);

        editTextNombre.setText(Constant.USER_NOMBRE);
        editTextApeellido.setText(Constant.USER_APELLIDO);
        editTextEmail.setText(Constant.USER_EMAIL);
        editTextCelular.setText(Constant.USER_CELULAR);
        Picasso.get()
                .load(Api.URL_IMAGE)
                .resize(100, 100)
                .into(imageViewPefil);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_perfil, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu_perfil) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {


        String url;
        HashMap<String, String> params;

        int requestCode;


        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode){
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if(object.getInt("error")== 0 ){
                  //  refreshContenidoList(object.getJSONArray("result"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(Void... voids) {

            RequestHandler requestHandler = new RequestHandler();
            if(requestCode == Api.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if ((requestCode == Api.CODE_GET_REQUEST))
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}
