package fpoly.edu.duan1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import fpoly.edu.duan1.DAO.GioHang_KHDAO;
import fpoly.edu.duan1.DAO.KhachHangDAO;
import fpoly.edu.duan1.Database.DBHelper;
import fpoly.edu.duan1.Fragment.DoanhThuFragment;
import fpoly.edu.duan1.Fragment.DoimkFM;
import fpoly.edu.duan1.Fragment.GioHangFragment;
import fpoly.edu.duan1.Fragment.HangSanPhamFragment;
import fpoly.edu.duan1.Fragment.HoaDonFragment;
import fpoly.edu.duan1.Fragment.SanPhamAdminFragment;
import fpoly.edu.duan1.Fragment.SanPhamFragment;

public class MainActivity extends AppCompatActivity {

    KhachHangDAO khachHangDAO;

    NavigationView navigationView;

    SharedPreferences nav_name;
    DrawerLayout drlayout;
    Toolbar tb;

    ImageView icongiohang;

    GioHang_KHDAO gioHang_khdao;
    private static final String PREF_ROLE_KEY = "user_role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Di chuyển đoạn mã đăng ký BroadcastReceiver và cập nhật badge vào đây
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Cập nhật giỏ hàng tại đây
                int count = gioHang_khdao.getSoLuongMasp();
                TextView badgeTextView = findViewById(R.id.badge);
                badgeTextView.setText(String.valueOf(count));
                badgeTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            }
        }, new IntentFilter("update_badge"));
        setContentView(R.layout.activity_main);
        drlayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        tb = findViewById(R.id.toolbar);
        icongiohang=findViewById(R.id.icongiohang);
        gioHang_khdao=new GioHang_KHDAO(getApplicationContext());
        hideSystemUI();
        //name user
        View nav_header = navigationView.getHeaderView(0);
        TextView txtten = nav_header.findViewById(R.id.txtten);
        nav_name = getSharedPreferences("name_Nav", MODE_PRIVATE);
        String hoten = nav_name.getString("makh", "");
        khachHangDAO = new KhachHangDAO(this);
        txtten.setText(khachHangDAO.getHotenByMatt(hoten));
        //giohang rieng user
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        String tableName = "Giohang_" + hoten;
        boolean tableExists = dbHelper.isTableExists(tableName);
        if (tableExists) {
            // Bảng đã được tạo
        } else {
            // Bảng chưa được tạo
            dbHelper.createNewTableFromThanhVien(hoten); // Tạo bảng
        }
        TextView badgeTextView = findViewById(R.id.badge);
// Set the badge count (replace 'count' with your actual count)
        int count = gioHang_khdao.getSoLuongMasp();
        badgeTextView.setText(String.valueOf(count));

// Show or hide the badge based on the count
        badgeTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //lấy dữ liệu admin role0
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userRole = sharedPreferences.getString(PREF_ROLE_KEY, "");
        Menu menu1 = navigationView.getMenu();
        if (!userRole.equals("admin")) {
            tb.setTitleTextColor(Color.BLACK);
            ab.setHomeAsUpIndicator(R.drawable.baseline_menu_24_black);
            menu1.findItem(R.id.sanphamadmin).setVisible(false);
            menu1.findItem(R.id.qlhang).setVisible(false);
            menu1.findItem(R.id.dt).setVisible(false);
            //set frag dau tien
            Fragment defaultFragment = new SanPhamFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, defaultFragment)
                    .commit();
            navigationView.setCheckedItem(R.id.sanpham); // Highlight the default item in the navigation drawer
            setTitle("Sản phẩm");
        } else {
            ab.setHomeAsUpIndicator(R.drawable.baseline_menu_24_white);
            tb.setBackgroundColor(Color.parseColor("#4BB9A0"));
            icongiohang.setVisibility(View.GONE);
            Fragment defaultFragment = new SanPhamAdminFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, defaultFragment)
                    .commit();
            navigationView.setCheckedItem(R.id.sanphamadmin); // Highlight the default item in the navigation drawer
            setTitle("Quản lí Sản phẩm");
            menu1.findItem(R.id.giohang).setVisible(false);
            menu1.findItem(R.id.sanpham).setVisible(false);
            menu1.findItem(R.id.hoadon).setVisible(false);
        }
        icongiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Giỏ hàng");
                Fragment gioHangFragment = new GioHangFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, gioHangFragment)
                        .commit();
                drlayout.closeDrawer(GravityCompat.START);
                navigationView.setCheckedItem(R.id.giohang);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                if (id == android.R.id.home)
                    drlayout.openDrawer(GravityCompat.START);
                if (id == R.id.sanpham) {
                    setTitle("Sản phẩm");
                    fragment = new SanPhamFragment();
                } else if (id == R.id.giohang) {
                    setTitle("Giỏ hàng");
                    fragment=new GioHangFragment();
                } else if (id == R.id.sanphamadmin) {
                    setTitle("Quản lí Sản phẩm");
                    fragment = new SanPhamAdminFragment();
                } else if (id == R.id.qlhang) {
                    setTitle("Quản lí hãng");
                    fragment =new HangSanPhamFragment();
                } else if (id == R.id.hoadon) {
                    fragment=  new HoaDonFragment();
                    setTitle("Hoá đơn");
                } else if (id == R.id.dt) {
                    setTitle("Doanh thu");
                    fragment =new DoanhThuFragment();
                } else if (id == R.id.dmk) {
                    setTitle("Đổi mật khẩu");
                    fragment= new DoimkFM();
                } else if (id == R.id.dx) {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
                if (fragment != null) {
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.flContent, fragment)
                            .commit();
                }
                drlayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            drlayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    private void hideSystemUI() {
// Đặt cho activity hiển thị toàn màn hình
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
}