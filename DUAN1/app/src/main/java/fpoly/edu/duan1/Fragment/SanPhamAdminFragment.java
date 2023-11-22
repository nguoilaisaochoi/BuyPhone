package fpoly.edu.duan1.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import fpoly.edu.duan1.Adapter.SanPhamAdminAdapter;
import fpoly.edu.duan1.DAO.SanPhamDAO;
import fpoly.edu.duan1.Model.HangSp;
import fpoly.edu.duan1.Model.SanPham;
import fpoly.edu.duan1.R;
import fpoly.edu.duan1.Adapter.HangspSpinnerAdapter;
import fpoly.edu.duan1.DAO.HangSpDAO;

public class SanPhamAdminFragment extends Fragment {
    ListView lvsp;

    static SanPhamDAO dao;

    ArrayList<SanPham> list;

    SanPhamAdminAdapter adapter;
    FloatingActionButton fab;
    Dialog dialog;

    Spinner sphang;
    EditText tensp, giasp, manhinh, ram, dungluong;
    TextView masp;
    ImageView anhsp;
    Button luusp, chonanh, huysp;

    HangspSpinnerAdapter hangspSpinnerAdapter;

    HangSpDAO hangSpDAO;

    ArrayList<HangSp> listHangSp;

    SearchView searchView;

    int mahang, positionhangsp;
    SanPham item;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String imagePath;

    Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.sanphamadminfragment, container, false);
        lvsp = v.findViewById(R.id.lvsp);
        dao = new SanPhamDAO(getActivity());
        fab = v.findViewById(R.id.fab);
        searchView = v.findViewById(R.id.searchViewad);
        capnhatlv();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(getActivity(), 0);
            }
        });

        lvsp.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                item = list.get(position);
                opendialog(getActivity(), 1);//1 update
                return false; // hoặc false tùy thuộc vào việc xử lý sự kiện của bạn
            }
        });
        lvsp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchView.clearFocus();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSanpham(newText);
                return false;
            }
        });
        return v;
    }

    void capnhatlv() {
        list = (ArrayList<SanPham>) dao.getAll();
        adapter = new SanPhamAdminAdapter(getActivity(), this, list);
        lvsp.setAdapter(adapter);
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    protected void opendialog(final Context context, final int type) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_sua_sanpham);
        masp = dialog.findViewById(R.id.tvmasp);
        tensp = dialog.findViewById(R.id.ettensp);
        sphang = dialog.findViewById(R.id.sp_hang);
        manhinh = dialog.findViewById(R.id.etmanhinh);
        ram = dialog.findViewById(R.id.etram);
        dungluong = dialog.findViewById(R.id.etdungluong);
        giasp = dialog.findViewById(R.id.etgia);
        chonanh = dialog.findViewById(R.id.btnanh);
        anhsp = dialog.findViewById(R.id.ivanh);
        luusp = dialog.findViewById(R.id.btnluu);
        huysp = dialog.findViewById(R.id.btnhuy);
        //
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(layoutParams);
        hangSpDAO = new HangSpDAO(context);
        listHangSp = new ArrayList<HangSp>();
        listHangSp = (ArrayList<HangSp>) hangSpDAO.getAll();
        hangspSpinnerAdapter = new HangspSpinnerAdapter(context, listHangSp);
        sphang.setAdapter(hangspSpinnerAdapter);
        sphang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mahang = listHangSp.get(position).getMahang();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        huysp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (type != 0) {
            for (int i = 0; i < listHangSp.size(); i++)
                if (item.getMahang() == (listHangSp.get(i).getMahang())) {
                    positionhangsp = i;
                }
            sphang.setSelection(positionhangsp);
            //anhsp.setImageURI(item.getAnh());
            if (item != null) {
                try {
                    Glide.with(context)
                            .load(item.getAnh())
                            .dontAnimate()
                            .override(290, 290)
                            .skipMemoryCache(false)
                            .error(R.drawable.baseline_image_24) // Add an error drawable
                            .diskCacheStrategy(DiskCacheStrategy.ALL) // Sử dụng cache đĩa
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    selectedImageUri=null;
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    selectedImageUri = Uri.parse(item.getAnh().toString());
                                    return false;
                                }
                            })
                            .into(anhsp);
                } catch (Exception e) {
                }
            }
            tensp.setText(item.getTensp());
            masp.setText(String.valueOf(item.getMasp()));
            masp.setVisibility(View.GONE);//masp bị ẩn
            manhinh.setText(String.valueOf(item.getManhinh()));
            ram.setText(String.valueOf(item.getRam()));
            dungluong.setText(String.valueOf(item.getDungluong()));
            giasp.setText(String.valueOf(item.getGia()));

        }
        luusp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item = new SanPham();
                item.setMahang(mahang);
                item.setTensp(tensp.getText().toString());
                item.setManhinh(Float.parseFloat(manhinh.getText().toString()));
                item.setRam(Integer.parseInt(ram.getText().toString()));
                item.setDungluong(Integer.parseInt(dungluong.getText().toString()));
                item.setGia(Integer.parseInt(giasp.getText().toString()));
                item.setAnh(selectedImageUri);
                // Lưu đường dẫn ảnh vào cơ sở dữ liệu
                if (type == 0) {
                    if (dao.insert(item) > 0) {
                        Context context = getContext();
                        LayoutInflater inflater = getLayoutInflater();
                        View customToastView = inflater.inflate(R.layout.customtoast, null);
                        TextView textView = customToastView.findViewById(R.id.custom_toast_message);
                        textView.setText("Đã thêm");

                        Toast customToast = new Toast(context);
                        customToast.setDuration(Toast.LENGTH_SHORT);
                        customToast.setView(customToastView);
                        customToast.show();
                        //Toast.makeText(context, "Đã thêm", Toast.LENGTH_SHORT).show();
                    } else {
                        Context context = getContext();
                        LayoutInflater inflater = getLayoutInflater();
                        View customToastView = inflater.inflate(R.layout.customtoast, null);
                        TextView textView = customToastView.findViewById(R.id.custom_toast_message);
                        textView.setText("Không thêm được");

                        Toast customToast = new Toast(context);
                        customToast.setDuration(Toast.LENGTH_SHORT);
                        customToast.setView(customToastView);
                        customToast.show();
                        //Toast.makeText(context, "Không thêm được", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    item.setMasp(Integer.parseInt(masp.getText().toString()));
                    if (dao.update(item) > 0) {
                        Context context = getContext();
                        LayoutInflater inflater = getLayoutInflater();
                        View customToastView = inflater.inflate(R.layout.customtoast, null);
                        TextView textView = customToastView.findViewById(R.id.custom_toast_message);
                        textView.setText("Đã sửa");

                        Toast customToast = new Toast(context);
                        customToast.setDuration(Toast.LENGTH_SHORT);
                        customToast.setView(customToastView);
                        customToast.show();
                        //Toast.makeText(context, "Đã sửa", Toast.LENGTH_SHORT).show();
                    } else {
                        Context context = getContext();
                        LayoutInflater inflater = getLayoutInflater();
                        View customToastView = inflater.inflate(R.layout.customtoast, null);
                        TextView textView = customToastView.findViewById(R.id.custom_toast_message);
                        textView.setText("Không sửa được");

                        Toast customToast = new Toast(context);
                        customToast.setDuration(Toast.LENGTH_SHORT);
                        customToast.setView(customToastView);
                        customToast.show();
                        //Toast.makeText(context, "Không sửa được", Toast.LENGTH_SHORT).show();
                    }
                }
                capnhatlv();
                dialog.dismiss();
            }
        });
        dialog.show();
        chonanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }


    public void xoa(final String ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Muốn xoá?!");
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dao.delete(ID);
                        capnhatlv();
                        dialog.cancel();
                    }
                }
        );
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        builder.show();
    }

    public boolean suaSanPham(int position) {
        item = list.get(position);
        opendialog(getActivity(), 1); // 1 update
        return false;
    }

    private void searchSanpham(String keyword) { //timkiem
        ArrayList<SanPham> searchResults = dao.searchSpByName(keyword);
        adapter.clear();
        adapter.addAll(searchResults);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("SanPhamAdminFragment", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                selectedImageUri = imageUri;
                // Hiển thị ảnh đã chọn trong ImageView
                anhsp.setImageURI(selectedImageUri);
            } else {
                // Xử lý lỗi khi không thể lấy được Uri từ Intent
                Toast.makeText(getActivity(), "Không thể lấy đường dẫn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
