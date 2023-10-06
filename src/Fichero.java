import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;

public class Fichero {
    static Scanner sc = new Scanner(System.in);
    private static List<Coche> cocheList = new ArrayList<>();
    private static  String fileName ;
    private final String campoClave;
    public static final byte BYTES_MATRICULA = 7;
    public static final byte BYTES_MODELO = 32;
    public static final byte BYTES_MARCA = 32;
    private static int longitudRegistro;
    private static long numeroRegistros;
    private static final int NUMBER_OF_BYTES = 71;

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
     *
     * @param coche
     * @param posicion
     * @param numeroBytes
     * @throws IOException
     */
    public static void insetar(Coche coche, int posicion, int numeroBytes) throws IOException {


        if (posicion < 0) {
            throw new IllegalArgumentException("La posición no es válida");
        } else if (numeroBytes < 0) {
            throw new IllegalArgumentException("El número de bytes no es válido");
        } else if (coche == null) {
            throw new IllegalArgumentException("El coche no puede ser null");
        }
        //Comprobamos que la longitud de los campos no sea mayor que la longitud de los bytes.
        if (coche.getTuition().getBytes().length > BYTES_MATRICULA) {
            throw new IllegalArgumentException("La matrícula no puede tener más de 7 bytes");
        } else if (coche.getModel().getBytes().length > BYTES_MODELO) {
            throw new IllegalArgumentException("El modelo no puede tener más de 32 bytes");
        } else if (coche.getBrand().getBytes().length > BYTES_MARCA) {
            throw new IllegalArgumentException("La marca no puede tener más de 32 bytes");
        } else {
            //si no es mayor lo rellenamos con espacios en blanco.
            coche.setTuition(String.format("%-7s", coche.getTuition()));
            coche.setModel(String.format("%-32s", coche.getModel()));
            coche.setBrand(String.format("%-32s", coche.getBrand()));
        }

        if (existeCoche(coche)) {
            System.err.println("El coche ya existe");
        } else {
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            //Nos posicionamos en la posición que queremos insertar el registro.
            raf.seek((long) posicion * longitudRegistro);
            raf.write(coche.getTuition().getBytes());
            raf.write(coche.getBrand().getBytes());
            raf.write(coche.getModel().getBytes());
            raf.close();
            //Lo utilizamos para saber lo que están repetidos.
        }

        cocheList.add(coche);

    }

    /**
     * Comprueba si un coche existe en el fichero.
     *
     * @param coche Coche que queremos comprobar si existe.
     * @return true si existe, false si no existe.
     */
    public static boolean existeCoche(Coche coche) {
        boolean existe = false;
        for (Coche c : cocheList) {
            if (c.equals(coche)) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    public static void borrar(int posicion, String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.seek((long) posicion * longitudRegistro);
        raf.write("       ".getBytes());
        raf.write("                                ".getBytes());
        raf.write("                                ".getBytes());
        raf.close();
    }

    public static void modificar(Coche coche, int posicion) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.seek((long) posicion * longitudRegistro);
        raf.write(coche.getTuition().getBytes());
        raf.write(coche.getBrand().getBytes());
        raf.write(coche.getModel().getBytes());
        raf.close();
    }

    public static void ordenarFichero(String fileName) throws IllegalArgumentException, IOException{

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            String[] campos;
            //Leemos el fichero y lo guardamos en una lista.
            while (line != null) {
                campos = line.split(" ");
                Coche coche = new Coche(campos[0], campos[1], campos[2]);
                cocheList.add(coche);
                line = br.readLine();
            }
            br.close();
            //Ordenamos la lista por matrícula con una clase anónima.
            Collections.sort(cocheList , new Comparator<Coche>() {
                @Override
                public int compare(Coche o1, Coche o2) {
                    o1.getTuition().
                    return o1.getTuition().compareTo(o2.getTuition());
                }
            });
            //Escribimos la lista ordenada en el fichero.
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            //Escribimos la lista ordenada en el fichero.
            for (Coche c : cocheList) {
                raf.write(c.getTuition().getBytes());
                raf.write(c.getBrand().getBytes());
                raf.write(c.getModel().getBytes());
            }
            raf.close();

    }

    /**
     * Lee un fichero CSV y lo muestra por pantalla.
     *
     * @throws IOException
     */
    public static String leerCSV(String nombreFichero) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(nombreFichero));
        String line = br.readLine();
        String[] campos;
        /*
        Mientras línea no sea null, es decir, que haya líneas que leer
        se ejecutará el bucle.
         */
        while (line != null) {
            //Dividimos la línea por comas.
            campos = line.split(",");
            //Mostramos los campos.
            System.out.println(campos[0] + " " + campos[1] + " " + campos[2]);
            //Leemos la siguiente línea.
            line = br.readLine();
        }
        //Cerramos el fichero.
        br.close();
        //Devolvemos la línea.
        return line;
    }

    public static void main(String[] args) {

        boolean salir = false;
        do {
            System.out.println(menu());
            System.out.println("Introduce una opción: ");
            int opcion = Integer.parseInt(sc.nextLine());
            System.out.println("Has elegido la opción " + opcion);
            System.out.println("Introduce el nombre del fichero");
            String fileName = sc.nextLine();
            switch (opcion) {
                case 1:
                    try {
                        do {
                            Map<String, Integer> datos = new HashMap<>();
                            datos.put("matricula", (int) BYTES_MATRICULA);
                            datos.put("modelo", (int) BYTES_MODELO);
                            datos.put("marca", (int) BYTES_MARCA);
                            Fichero fichero = new Fichero(fileName, datos, "matricula");
                            int posicion = 0;
                            System.out.println("Introduce la matrícula");
                            String matricula = sc.nextLine();
                            System.out.println("Introduce la marca");
                            String marca = sc.nextLine();
                            System.out.println("Introduce el modelo");
                            String modelo = sc.nextLine();
                            System.out.println("Introduce la posición donde quieres insertar el coche");
                            posicion = Integer.parseInt(sc.nextLine());
                            Coche coche = new Coche(marca, modelo, matricula);
                            insetar(coche, posicion, NUMBER_OF_BYTES);
                            System.out.println("Quieres seguir insertando coches? (s/n)");
                        } while (sc.nextLine().equalsIgnoreCase("s"));


                    } catch (IOException e) {
                        System.err.println("Error al crear el fichero");
                    }


                    break;
                case 2:
                    System.out.println("Introduce la posición que quieres borrar");
                    int posicion = Integer.parseInt(sc.nextLine());
                    try {
                        borrar(posicion,fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("Introduce la posición que quieres modificar");
                    posicion = Integer.parseInt(sc.nextLine());
                    System.out.println("Introduce la matrícula");
                    String matricula = sc.nextLine();
                    System.out.println("Introduce la marca");
                    String marca = sc.nextLine();
                    System.out.println("Introduce el modelo");
                    String modelo = sc.nextLine();
                    Coche coche = new Coche(marca, modelo, matricula);
                    try {
                        modificar(coche, posicion);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("Introduce el fichero que quieres ordenar");
                    String fichero = sc.nextLine();
                    try {
                        ordenarFichero(fichero);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("Introduce el ficher que quieres leer");
                    String ficheroLeer = sc.nextLine();
                    try {
                        leerCSV(ficheroLeer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    salir = true;
                    break;
            }
        }while (!salir);
    }

    public static String menu() {
        StringBuilder sb = new StringBuilder();
        sb.append("1. Insertar coche\n");
        sb.append("2. Borrar coche\n");
        sb.append("3. Modificar coche\n");
        sb.append("4. Ordenar coches\n");
        sb.append("5. Cargar coches\n");
        sb.append("6. Salir\n");
        return sb.toString();
    }
}
