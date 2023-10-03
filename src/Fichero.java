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
    public static final int BYTES_MATRICULA = 7;
    public static final int BYTES_MODELO = 32;
    public static final int BYTES_MARCA = 32;
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
     * Recupera un registro del fichero a partir de su clave.
     *
     * @param clave Clave del registro que queremos recuperar.
     * @return Map con los datos del registro.
     * @throws IOException
     */
   // public Map<String, String> recuperar(String clave) throws IOException {

//        int posicion = 0;
//        boolean encontrado = false;
//        Map<String, String> resultado;
//        //Try with resources para cerrar el fichero.
//        try (FileInputStream fileInputStream = new FileInputStream(this.fileName)) {
//            resultado = null;
//            //Mientras no lleguemos al final del fichero y no hayamos encontrado el registro.
//            while (posicion < numeroRegistros && !encontrado) {
//                //Creamos un array de bytes con la longitud del registro.
//                byte[] registro = new byte[this.longitudRegistro];
//                /*
//                Si el número de bytes leidos es menor que la longitud del registro es que hemos llegado al final del fichero.
//                Por lo que devolvemos null.
//                 */
//                if (fileInputStream.read(registro, 0, this.longitudRegistro) < this.longitudRegistro) {
//                    return null;
//                }
//
//                int posicionCampo = 0;
//                String valorCampo = null;
//                /*
//                Recorremos el mapa de datos para recuperar los campos del registro y si el campo actual es el campo clave
//                comprobamos si es el registro que estamos buscando y si es así lo devolvemos.
//                 */
//                for (Map.Entry<String, Integer> campo : datos.entrySet()) {
//                    String campoActual = campo.getKey();
//                    int longitudCampo = campo.getValue();
//                    //Si el campo actual es el campo clave lo recuperamos.
//                    if (campoActual.equals(campoClave)) {
//                        valorCampo = new String(registro, posicionCampo, longitudCampo, StandardCharsets.UTF_8);
//                        break;
//                    }
//                    //Incrementamos la posición del campo.
//                    posicionCampo += longitudCampo;
//                }
//
//                if (clave.equals(valorCampo)) {
//                    resultado = new HashMap<>();
//                    posicionCampo = 0;
//                    //Recorremos el mapa de datos para recuperar los campos del registro.
//                    for (Map.Entry<String, Integer> campo : datos.entrySet()) {
//                        String campoActual = campo.getKey();
//                        int longitudCampo = campo.getValue();
//                        String valor = new String(registro, posicionCampo, longitudCampo, StandardCharsets.UTF_8);
//                        resultado.put(campoActual, valor);
//                        //Incrementamos la posición del campo.
//                        posicionCampo += longitudCampo;
//                    }
//                    encontrado = true;
//                }
//            }
//            return resultado;
//        }
//    }

    /**
     * Inserta un registro en el fichero en un lugar concreto del fichero teniendo en cuenta
     * los bytes de los registros 7 bytes para matrícula, 32 bytes para modelo y 32 bytes para marca.
     */
    public void insetar(Coche coche,int posicion,int numeroBytes) throws IOException {

        if(posicion < 0 || posicion > numeroRegistros){
            throw new IllegalArgumentException("La posición no es válida");
        }else if(numeroBytes < 0 || numeroBytes > longitudRegistro){
            throw new IllegalArgumentException("El número de bytes no es válido");
        }else if(coche == null){
            throw new IllegalArgumentException("El coche no puede ser null");
        }else if(coche.getTuition().length() > BYTES_MATRICULA){
            throw new IllegalArgumentException("La matrícula debe tener 7 caracteres");
        }else if(coche.getModel().length() > BYTES_MODELO){
            throw new IllegalArgumentException("El modelo debe tener 32 caracteres");
        } else if (coche.getBrand().length() > BYTES_MARCA) {
            throw new IllegalArgumentException("La marca debe tener 32 caracteres");
        }else if(cocheList.contains(coche)){
            throw new IllegalArgumentException("El coche ya existe");
        }else{
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            raf.seek((long) posicion * longitudRegistro);
            raf.write(coche.getTuition().getBytes());
            raf.write(coche.getModel().getBytes());
            raf.write(coche.getBrand().getBytes());
            raf.close();
            cocheList.add(coche);
        }


    }


    public static void main(String[] args) {

        try {
            Map<String, Integer> datos = new HashMap<>();
            datos.put("matricula", BYTES_MATRICULA);
            datos.put("modelo", BYTES_MODELO);
            datos.put("marca", BYTES_MARCA);
            Fichero fichero = new Fichero("coches.dat", datos, "matricula");
            Coche coche = new Coche("Seat", "Ibiza", "1234ABC");
            fichero.insetar(coche, 0, fichero.NUMBER_OF_BYTES);
            Coche coche2 = new Coche("Seat", "Ibiza", "1234ABC");
            fichero.insetar(coche2, 0, fichero.NUMBER_OF_BYTES);
            Coche coche3 = new Coche("Seat", "Leon", "1684ABC");
            fichero.insetar(coche3, 0, fichero.NUMBER_OF_BYTES);
            System.out.println("Coche insertado");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
