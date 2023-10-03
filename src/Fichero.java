import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class Fichero {
    private final String fileName;
    private final Map<String, Integer> datos;

    private final String campoClave;

    private int longitudRegistro;
    private long numeroRegistros;
    private long numeroRegistroBorrado;

    public String getFileName() {
        return fileName;
    }

    public Map<String, Integer> getDatos() {
        return datos;
    }

    public String getCampoClave() {
        return campoClave;
    }

    public int getLongitudRegistro() {
        return longitudRegistro;
    }

    public long getNumeroRegistros() {
        return numeroRegistros;
    }

    public long getNumeroRegistroBorrado() {
        return numeroRegistroBorrado;
    }

    public void setLongitudRegistro(int longitudRegistro) {
        this.longitudRegistro = longitudRegistro;
    }

    public void setNumeroRegistros(long numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

    public void setNumeroRegistroBorrado(long numeroRegistroBorrado) {
        this.numeroRegistroBorrado = numeroRegistroBorrado;
    }

    public Fichero(String fileName, Map<String, Integer> datos, String campoClave) throws IOException {
        this.fileName = fileName;
        this.datos = datos;
        this.campoClave = campoClave;
        longitudRegistro = 0;
        numeroRegistros = 0;
        numeroRegistroBorrado = 0;

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
    public Map<String, String> recuperar(String clave) throws IOException {

        int posicion = 0;
        boolean encontrado = false;
        Map<String, String> resultado;
        //Try with resources para cerrar el fichero.
        try (FileInputStream fileInputStream = new FileInputStream(this.fileName)) {
            resultado = null;
            //Mientras no lleguemos al final del fichero y no hayamos encontrado el registro.
            while (posicion < numeroRegistros && !encontrado) {
                //Creamos un array de bytes con la longitud del registro.
                byte[] registro = new byte[this.longitudRegistro];
                /*
                Si el número de bytes leidos es menor que la longitud del registro es que hemos llegado al final del fichero.
                Por lo que devolvemos null.
                 */
                if (fileInputStream.read(registro, 0, this.longitudRegistro) < this.longitudRegistro) {
                    return null;
                }

                int posicionCampo = 0;
                String valorCampo = null;
                /*
                Recorremos el mapa de datos para recuperar los campos del registro y si el campo actual es el campo clave
                comprobamos si es el registro que estamos buscando y si es así lo devolvemos.
                 */
                for (Map.Entry<String, Integer> campo : datos.entrySet()) {
                    String campoActual = campo.getKey();
                    int longitudCampo = campo.getValue();
                    //Si el campo actual es el campo clave lo recuperamos.
                    if (campoActual.equals(campoClave)) {
                        valorCampo = new String(registro, posicionCampo, longitudCampo, StandardCharsets.UTF_8);
                        break;
                    }
                    //Incrementamos la posición del campo.
                    posicionCampo += longitudCampo;
                }

                if (clave.equals(valorCampo)) {
                    resultado = new HashMap<>();
                    posicionCampo = 0;
                    //Recorremos el mapa de datos para recuperar los campos del registro.
                    for (Map.Entry<String, Integer> campo : datos.entrySet()) {
                        String campoActual = campo.getKey();
                        int longitudCampo = campo.getValue();
                        String valor = new String(registro, posicionCampo, longitudCampo, StandardCharsets.UTF_8);
                        resultado.put(campoActual, valor);
                        //Incrementamos la posición del campo.
                        posicionCampo += longitudCampo;
                    }
                    encontrado = true;
                }
            }
            return resultado;
        }
    }

    /**
     * Inserta un registro en el fichero en un lugar dado.
     *
     * @param registro Map con los datos del registro.
     * @return Posición del registro en el fichero.
     * @throws IOException
     */
    public long insetar(Map<String, String> registro) {
        long posicion = 1;
        //RandomAccessFile para poder posicionarnos en el fichero y escribir en él.
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.fileName, "rw")) {
            //Nos posicionamos al final del fichero.
            randomAccessFile.seek(randomAccessFile.length());
            //Recorremos el mapa de datos para insertar los campos del registro.
            for (Map.Entry<String, String> campo : registro.entrySet()) {
                String campoActual = campo.getKey();
                String valor = campo.getValue();
                //Si el campo actual es el campo clave lo insertamos.
                if (campoActual.equals(campoClave)) {
                    posicion = randomAccessFile.getFilePointer();
                }
                //Insertamos el campo.
                randomAccessFile.write(valor.getBytes(StandardCharsets.UTF_8));
            }
            //Incrementamos el número de registros.
            numeroRegistros++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posicion;
    }


    public static void main(String[] args) {
        try {
            Map<String,Integer> campos = new HashMap<String,Integer>();
            campos.put("DNI", 9);
            campos.put("NOMBRE", 32);
            campos.put("CP", 5);
            Fichero fsno = new Fichero("fic_sec_ord.dat", campos, "MATRICULA");
            Map reg = new HashMap();
            reg.put("DNI", "56789012B");
            reg.put("NOMBRE", "SAMPER");
            reg.put("CP", "29730");
            if (fsno.insetar((HashMap<String, String>) reg) < 0) {
                System.err.println("No se pudo insertar registro, clave duplicada: " + reg.get("DNI"));
            }
            reg.clear();
            reg.put("DNI", "89012345E");
            reg.put("NOMBRE", "ROJAS");
            reg.put("CP", "29730");
            if (fsno.insetar((HashMap<String, String>) reg) < 0) {
                System.err.println("No se pudo insertar registro, clave duplicada: " + reg.get("DNI"));
            }
            reg.clear();
            reg.put("DNI", "23456789D");
            reg.put("NOMBRE", "DORCE");
            reg.put("CP", "13700");
            if (fsno.insetar((HashMap<String, String>) reg) < 0) {
                System.err.println("No se pudo insertar registro, clave duplicada: " + reg.get("DNI"));
            }
            reg.clear();
            reg.put("DNI", "78901234X");
            reg.put("NOMBRE", "NADALES");
            reg.put("CP", "44126");
            if (fsno.insetar((HashMap<String, String>) reg) < 0) {
                System.err.println("No se pudo insertar registro, clave duplicada: " + reg.get("DNI"));
            }
            reg.clear();
            reg.put("DNI", "12345678Z");
            reg.put("NOMBRE", "ARCOS");
            reg.put("CP", "29730");
            if (fsno.insetar((HashMap<String, String>) reg) <0) {
                System.err.println("No se pudo insertar registro, clave duplicada: " + reg.get("DNI"));
            }
            reg.clear();
            System.out.println("Fichero " + fsno.getFileName()
                    + " contiene " + fsno.getNumeroRegistros() + " registros.");
            // Se marcan como borrados un registro de enmedio, el del principio y el del final.
//            String DNIParaBorrar = "23456789D";
//            if (fsno.borrar(DNIParaBorrar)) {
//                System.out.println("Borrado registro para DNI: " + DNIParaBorrar);
//            } else {
//                System.out.println("No se encuentra DNI " + DNIParaBorrar + " para borrar registro.");
//            }
//            DNIParaBorrar = "56789012B";
//            if (fsno.borrar(DNIParaBorrar)) {
//                System.out.println("Borrado registro para DNI: " + DNIParaBorrar);
//            } else {
//                System.out.println("No se encuentra DNI " + DNIParaBorrar + " para borrar registro.");
//            }
//            DNIParaBorrar = "12345678Z";
//            if (fsno.borrar(DNIParaBorrar)) {
//                System.out.println("Borrado registro para DNI: " + DNIParaBorrar);
//            } else {
//                System.out.println("No se encuentra DNI " + DNIParaBorrar + " para borrar registro.");
//            }
//            System.out.println(fsno.getNumReg() + " registros en fichero, de los que " + fsno.getNumRegMarcadosBorrado() + " registros están marcados como borrados.");



        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
