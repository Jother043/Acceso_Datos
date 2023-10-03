public class Coche {

    private String brand;
    private String model;
    private String tuition;

    public Coche(String brand, String model, String tuition) {
        this.brand = brand;
        this.model = model;
        this.tuition = tuition;
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
}
