package fpoly.edu.duan1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fpoly.edu.duan1.DAO.KhachHangDAO;
import fpoly.edu.duan1.Model.KhachHang;
public class Dangki extends AppCompatActivity {

    EditText tk,hoten,mk,rmk;

    KhachHangDAO dao;
    KhachHang item;

    Button btndk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manhinh_dangky);
        tk=findViewById(R.id.edt_emailLogin);
        mk=findViewById(R.id.dkmatkhau);
        hoten=findViewById(R.id.hoten);
        rmk=findViewById(R.id.dkllmathau);
        btndk=findViewById(R.id.btn_dangki);
        hideSystemUI();
        dao = new KhachHangDAO(getApplicationContext());
        btndk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valiable() > 0) {
                    item= new KhachHang();
                    item.setMakh(tk.getText().toString());
                    item.setMatKhau(mk.getText().toString());
                    item.setHoTen(hoten.getText().toString());
                    if (dao.insert(item) > 0) {
                        Toast.makeText(getApplicationContext(), "Tạo thành công", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public int valiable() {
        int check = 1;
        if (tk.getText().length() == 0 || mk.getText().length() == 0  || rmk.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Không bỏ trống", Toast.LENGTH_SHORT).show();
            check = -1;
        }

        if (!mk.getText().toString().equals(rmk.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show();
            check = -1;
        }
        return check;
    }
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}