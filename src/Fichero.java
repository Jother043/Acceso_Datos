import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class Fichero {
    private static List<Coche> cocheList;
    private final String fileName;
    private final String campoClave;
    public static final byte BYTES_MATRICULA = 7;
    public static final byte BYTES_MODELO = 32;
    public static final byte BYTES_MARCA = 32;
    private int longitudRegistro;
    private long numeroRegistros;
    private final int NUMBER_OF_BYTES = 71;

    public String getFileName() {
        return fileName;
    }




    public Fichero(String fileName, Map<String, Integer> datos, String campoClave) throws IOException {
        this.fileName = fileName;
        this.campoClave = campoClave;
        longitudRegistro = 0;
        numeroRegistros = 0;
        cocheList = new ArrayList<>();

        // Calculate the length of the record.
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            longitudRegistro += entry.getValue();
        }

        File f = new File(fileName);

        //Si el fichero existe calculamos la longitud del fichero y el numero de registros que tiene el fichero.
        if (f.exists()) {
            /*
            Calculamos el número de registros que tiene el fichero.
            Para ello dividimos la longitud del fichero entre la longitud del registro.
             */
            numeroRegistros = f.length() / longitudRegistro;
        } else {
            //Si el fichero no existe lo creamos.
            f.createNewFile();
        }

    }


    /**
     * Inserta un coche en el fichero.
     * Si el coche ya existe no lo inserta.
     * Si la posición es menor que 0 lanza una excepción.
     * Si el número de bytes es menor que 0 o mayor que la longitud del registro lanza una excepción.
     * Si el coche es null lanza una excepción.
     * Si la longitud de la matrícula es mayor que 7 lanza una excepción.
     * Si la longitud del modelo es mayor que 32 lanza una excepción.
     * Si la longitud de la marca es mayor que 32 lanza una excepción.
     * Si no se cumple ninguna de las condiciones anteriores inserta el coche en el fichero.
     * @param coche
     * @param posicion
     * @param numeroBytes
     * @throws IOException
     */
    public void insetar(Coche coche,int posicion,int numeroBytes) throws IOException {


        if(posicion < 0 ){
            throw new IllegalArgumentException("La posición no es válida");
        }else if(numeroBytes < 0 || numeroBytes > longitudRegistro){
            throw new IllegalArgumentException("El número de bytes no es válido");
        }else if(coche == null){
            throw new IllegalArgumentException("El coche no puede ser null");
        }
        //Comprobamos que la longitud de los campos no sea mayor que la longitud de los bytes.
        if(coche.getTuition().getBytes().length > BYTES_MATRICULA) {
            throw new IllegalArgumentException("La matrícula no puede tener más de 7 bytes");
        }else if(coche.getModel().getBytes().length > BYTES_MODELO){
            throw new IllegalArgumentException("El modelo no puede tener más de 32 bytes");
        }else if(coche.getBrand().getBytes().length > BYTES_MARCA){
            throw new IllegalArgumentException("La marca no puede tener más de 32 bytes");
        }else{
            //si no es mayor lo rellenamos con espacios en blanco.
            coche.setTuition(String.format("%-7s", coche.getTuition()));
            coche.setModel(String.format("%-32s", coche.getModel()));
            coche.setBrand(String.format("%-32s", coche.getBrand()));
        }

        //Si existe el coche no lo insertamos.
        if(existeCoche(coche)) {
            System.err.println("El coche ya existe");
        }else{
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            //Nos posicionamos en la posición que queremos insertar el registro.
            raf.seek((long) posicion * longitudRegistro);
            raf.write(coche.getTuition().getBytes());
            raf.write(coche.getBrand().getBytes());
            raf.write(coche.getModel().getBytes());
            raf.close();
            //Lo utilizamos para saber lo que están repetidos.
            cocheList.add(coche);
        }


    }

    /**
     * Comprueba si un coche existe en el fichero.
     * @param coche Coche que queremos comprobar si existe.
     * @return true si existe, false si no existe.
     */
    public boolean existeCoche(Coche coche){
        boolean existe = false;
        for (Coche c : cocheList) {
            if(c.equals(coche)){
                existe = true;
                break;
            }
        }
        return existe;
    }

    public static void main(String[] args) {

        try {
            Map<String, Integer> datos = new HashMap<>();
            datos.put("matricula", (int) BYTES_MATRICULA);
            datos.put("modelo", (int) BYTES_MODELO);
            datos.put("marca", (int) BYTES_MARCA);
            Fichero fichero = new Fichero("coches.csv", datos, "matricula");
            Coche coche = new Coche("Seat", "Ibiza", "1234ABC");
            fichero.insetar(coche, 0, fichero.NUMBER_OF_BYTES);
            Coche coche2 = new Coche("Seat", "Arona", "2345ABC");
            fichero.insetar(coche2, 1, fichero.NUMBER_OF_BYTES);
            Coche coche3 = new Coche("Seat", "Leon", "1684ABC");
            fichero.insetar(coche3, 2, fichero.NUMBER_OF_BYTES);
            Coche coche4 = new Coche("Seat", "Ateca", "6984ABC");
            fichero.insetar(coche4, 3, fichero.NUMBER_OF_BYTES);
            Coche coche5 = new Coche("Citroen", "Xsara", "3546LHF");
            fichero.insetar(coche5, 4, fichero.NUMBER_OF_BYTES);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
