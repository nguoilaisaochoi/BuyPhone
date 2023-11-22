package fpoly.edu.duan1.Model;

public class KhachHang {
    private String makh;
    private String hoTen;
    private Integer sdt;
    private String matKhau;


    public KhachHang() {
    }

    public KhachHang(String makh, String hoTen, Integer sdt, String matKhau) {
        this.makh = makh;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.matKhau = matKhau;
    }

    public String getMakh() {
        return makh;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Integer getSdt() {
        return sdt;
    }

    public void setSdt(Integer sdt) {
        this.sdt = sdt;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
