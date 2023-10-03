import java.util.List;
import java.util.Objects;

public class Coche {

    private String brand;
    private String model;
    private String tuition;

    private static List<Coche> carList;

    public Coche(String brand, String model, String tuition) {
        this.brand = brand;
        this.model = model;
        this.tuition = tuition;
    }

    public static List<Coche> getCarList() {
        return carList;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getTuition() {
        return tuition;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    @Override
    public String toString() {
        return "Coche{" + "brand=" + brand + ", model=" + model + ", tuition=" + tuition + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coche coche = (Coche) o;
        return Objects.equals(brand, coche.brand) && Objects.equals(model, coche.model) && Objects.equals(tuition, coche.tuition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, tuition);
    }
}
