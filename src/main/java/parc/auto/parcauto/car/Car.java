package parc.auto.parcauto.car;

import parc.auto.parcauto.user.User;

import javax.persistence.*;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 64)
    private String marca;

    @Column(nullable = false, length = 64)
    private String model;

    @Column(nullable = false)
    private int an;

    @Column(nullable = false)
    private int km;

    @Column(nullable = false, length = 64)
    private String capacitateMotor;

    @Column(nullable = false, length = 64)
    private String combustibil;

    @Column(nullable = false)
    private int pret;

    @Column(nullable = false, length = 64)
    private String img;

    @Column(nullable = false, length = 512)
    private String descriere;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCapacitateMotor() {
        return capacitateMotor;
    }

    public void setCapacitateMotor(String capacitateMotor) {
        this.capacitateMotor = capacitateMotor;
    }

    public String getCombustibil() {
        return combustibil;
    }

    public void setCombustibil(String combustibil) {
        this.combustibil = combustibil;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public int getAn() {
        return an;
    }

    public void setAn(int an) {
        this.an = an;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public int getPret() {
        return pret;
    }

    public void setPret(int pret) {
        this.pret = pret;
    }

    @Transient
    public String getImgPath() {
        if(img == null || id == null) return null;
        return "/car-img/" + id + "/" + img;
    }
}
