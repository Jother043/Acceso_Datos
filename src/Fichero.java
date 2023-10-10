import java.util.*;
import java.io.*;


public class Fichero {
    static Scanner sc = new Scanner(System.in);
    private static List<Coche> cocheList = new ArrayList<>();
    private static String fileName;
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

    // Constructor
    public Fichero(String fileName, Map<String, Integer> datos, String campoClave) throws IOException {
        Fichero.fileName = fileName;
        this.campoClave = campoClave;
        longitudRegistro = 0;
        numeroRegistros = 0;
        cocheList = new LinkedList<>();

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
     * @param coche;
     * @param posicion;
     * @param numeroBytes;
     * @throws IOException;
     */
    public static void insertar(Coche coche, int posicion, int numeroBytes) throws IOException {

        if (posicion < 0) {
            throw new IllegalArgumentException("La posición no es válida");
        } else if (numeroBytes < 0) {
            throw new IllegalArgumentException("El número de bytes no es válido");
        } else if (coche == null) {
            throw new IllegalArgumentException("El coche no puede ser null");
        }

        // Comprobamos que la longitud de los campos no sea mayor que la longitud de los bytes.
        Validaciones(coche);
        //Cargamos el fichero en la lista.
        leerFichero(fileName);

        if (existeCoche(coche)) {
            System.err.println("El coche ya existe");
        } else {
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            // Calculamos el número de coches que tiene el fichero.
            int numCoches = (int) raf.length() / longitudRegistro;

            if (posicion < numCoches) {

                for (int i = numCoches - 1; i >= posicion; i--) {
                    raf.seek((long) i * longitudRegistro);
                    byte[] buffer = new byte[longitudRegistro];
                    raf.read(buffer);
                    raf.seek((long) (i + 1) * longitudRegistro);
                    raf.write(buffer);
                }
            }

            // Escribir el nuevo coche en la posición deseada
            raf.seek((long) posicion * longitudRegistro);
            raf.write(coche.getTuition().getBytes());
            raf.write(coche.getBrand().getBytes());
            raf.write(coche.getModel().getBytes());

            raf.close();
        }
    }

    /**
     * Comprueba que la longitud de los campos no sea mayor que la longitud de los bytes.
     * @param coche;
     */
    private static void Validaciones(Coche coche) {
        if (coche.getTuition().getBytes().length > BYTES_MATRICULA) {
            throw new IllegalArgumentException("La matrícula no puede tener más de 7 bytes");
        } else if (coche.getModel().getBytes().length > BYTES_MODELO) {
            throw new IllegalArgumentException("El modelo no puede tener más de 32 bytes");
        } else if (coche.getBrand().getBytes().length > BYTES_MARCA) {
            throw new IllegalArgumentException("La marca no puede tener más de 32 bytes");
        } else {
            // Si no es mayor, lo rellenamos con espacios en blanco.
            coche.setTuition(String.format("%-7s", coche.getTuition()));
            coche.setModel(String.format("%-32s", coche.getModel()));
            coche.setBrand(String.format("%-32s", coche.getBrand()));
        }
    }


    /**
     * Comprueba si un coche existe en el fichero.
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

    /**
     * Borra un coche del fichero volcando el contenido del fichero en una lista
     * y sobreescribiendo el fichero con el contenido de la lista sin el coche que queremos borrar.
     * @param posicion Posición del coche que queremos borrar.
     * @throws IOException;
     */
    public static void borrar(int posicion, String fileName, Coche coche) throws IOException {

        //Leemos el fichero y lo volcamos en una lista
        leerFichero(fileName);

        if (posicion < 0) {
            throw new IllegalArgumentException("La posición no es válida");
        } else if (posicion > cocheList.size()) {
            throw new IllegalArgumentException("La posición no es válida");
        } else if (coche == null) {
            throw new IllegalArgumentException("El coche no puede ser null");
        }

        Validaciones(coche);

        if (!existeCoche(coche)) {
            throw new IllegalArgumentException("El coche no existe");
        } else {
            File file = new File(fileName);
            file.delete();
            file.createNewFile();
            cocheList.remove(coche);
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");

            for (int i = 0; i < cocheList.size(); i++) {
                raf.seek((long) i * longitudRegistro);
                raf.write(String.format("%-7s", cocheList.get(i).getTuition()).getBytes());
                raf.write(String.format("%-32s", cocheList.get(i).getBrand()).getBytes());
                raf.write(String.format("%-32s", cocheList.get(i).getModel()).getBytes());
            }

            raf.close();
        }
    }

    /**
     * Lee el fichero y lo vuelca en una lista.
     *
     * @param fileName Nombre del fichero que queremos leer.
     */
    private static void leerFichero(String fileName) throws IOException {
        cocheList.clear();
        existeFichero(fileName);
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        int numCoches = (int) raf.length() / longitudRegistro;
        for (int i = 0; i < numCoches; i++) {
            raf.seek((long) i * longitudRegistro);
            byte[] buffer = new byte[longitudRegistro];
            raf.read(buffer);
            String tuition = new String(buffer, 0, BYTES_MATRICULA);
            String brand = new String(buffer, BYTES_MATRICULA, BYTES_MARCA);
            String model = new String(buffer, BYTES_MATRICULA + BYTES_MARCA, BYTES_MODELO);
            cocheList.add(new Coche(brand, model, tuition));
        }
        raf.close();

    }

    /**
     * Modifica un coche del fichero.
     *
     * @param coche    Coche que queremos modificar.
     * @param posicion Posición del coche que queremos modificar.
     * @throws IOException;
     */
    public static void modificar(Coche coche, int posicion, String fileName) throws IOException {
        leerFichero(fileName);
        Validaciones(coche);
        //Actualizamos la lista con el coche modificado.
        cocheList.set(posicion, coche);
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        for (int i = 0; i < cocheList.size(); i++) {
            raf.seek((long) i * longitudRegistro);
            raf.write(String.format("%-7s", cocheList.get(i).getTuition()).getBytes());
            raf.write(String.format("%-32s", cocheList.get(i).getBrand()).getBytes());
            raf.write(String.format("%-32s", cocheList.get(i).getModel()).getBytes());
        }
        raf.close();
    }

    /**
     * Ordena un fichero por matrícula usando una clase anónima (Intellij, me aconsejaba cambiarlo a una expresión lambda).
     * @param fileName Nombre del fichero que queremos ordenar.
     * @throws IllegalArgumentException;
     * @throws IOException;
     */
    public static void ordenarFichero(String fileName) throws IllegalArgumentException, IOException {
        leerFichero(fileName);
        //Expresión lambda para ordenar por matrícula.
        cocheList.sort(Comparator.comparing(Coche::getTuition));
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        for (int i = 0; i < cocheList.size(); i++) {
            raf.seek((long) i * longitudRegistro);
            raf.write(String.format("%-7s", cocheList.get(i).getTuition()).getBytes());
            raf.write(String.format("%-32s", cocheList.get(i).getBrand()).getBytes());
            raf.write(String.format("%-32s", cocheList.get(i).getModel()).getBytes());
        }
        raf.close();
    }

    /**
     * Lee un fichero CSV y lo muestra por pantalla.
     * También escribe el resultado en un fichero.
     * @param nombreFichero;
     * @throws IOException;
     */
    public static void leerCSV(String nombreFichero,String fileName) throws IOException {
        List<Coche> cocheList = new ArrayList<>();
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
            //Comprobamos que no sea la primera línea, ya que es la cabecera.
            if (!(campos[0].equals("Matricula") || campos[1].equals("Marca") || campos[2].equals("Modelo"))) {
                //Mostramos los campos.
                System.out.println(campos[1] + " " + campos[2] + " " + campos[0]);
                //Creamos un coche con los campos que hemos leido.
                Coche coche = new Coche(campos[1], campos[2], campos[0]);
                //Añadimos el coche a la lista.
                cocheList.add(coche);
            }
            //Leemos la siguiente línea.
            line = br.readLine();
        }
        //Cerramos el fichero.
        br.close();
        //Escribimos el resultado que hemos leido en un fichero csv.
        //TODO "Hacer que el fichero sea creado por el usuario".
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");

        //Recorremos la lista de coches volcada desde el método leerFichero.
        for (Coche coche : cocheList) {
            //Si el coche no existe en el fichero, lo escribimos y si existe, no lo escribimos.
            if (!buscar(coche.getTuition(), fileName)) {
                raf.seek(raf.length());
                raf.write(String.format("%-7s", coche.getTuition()).getBytes());
                raf.write(String.format("%-32s", coche.getBrand()).getBytes());
                raf.write(String.format("%-32s", coche.getModel()).getBytes());
            } else {
                System.out.println("El coche con matrícula " + coche.getTuition() + " ya existe en el fichero.");
            }
        }

    }

    /**
     * Busca un coche en el fichero.
     *
     * @param tuition Matrícula del coche que queremos buscar.
     * @param s       Nombre del fichero en el que queremos buscar.
     * @return true si el coche existe en el fichero y false si no existe.
     */
    private static boolean buscar(String tuition, String s) throws IOException {
        boolean encontrado = false;
        //Leemos el fichero y lo volcamos en una lista.
        leerFichero(s);
        for (Coche coche : cocheList) {
            if (coche.getTuition().equals(tuition)) {
                encontrado = true;
                break;
            }
        }
        return encontrado;
    }

    /**
     * Este método se encarga de saber si el fichero existe.
     * Si no existe nos lanza una excepción.
     * @param fileName;
     * @throws IOException;
     */
    public static void existeFichero(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException("El fichero no existe");
        }
    }


    public static void main(String[] args) {


        boolean salir = false;
        do {
            try {
                System.out.println(menu());
                System.out.println("Introduce una opción: ");
                int opcion = Integer.parseInt(sc.nextLine());
                System.out.println("Has elegido la opción " + opcion);
                System.out.println("Introduce el nombre del fichero");
                String fileName = sc.nextLine();
                switch (opcion) {
                    case 1 -> {
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
                                insertar(coche, posicion, NUMBER_OF_BYTES);
                                System.out.println("Quieres seguir insertando coches? (s/n)");
                            } while (sc.nextLine().equalsIgnoreCase("s"));


                        } catch (IOException e) {
                            System.err.println("Error al crear el fichero");
                        }
                    }
                    case 2 -> {
                        try {
                            //Creamos un mapa con los datos que queremos guardar en el fichero.
                            Map<String, Integer> datos = new HashMap<>();
                            datos.put("matricula", (int) BYTES_MATRICULA);
                            datos.put("modelo", (int) BYTES_MODELO);
                            datos.put("marca", (int) BYTES_MARCA);
                            existeFichero(fileName);
                            //Creamos el fichero.
                            Fichero fichero = new Fichero(fileName, datos, "matricula");
                            leerFichero(fileName);
                            System.out.println("Que coche quieres borrar?");
                            for (Coche c : cocheList) {
                                System.out.println(c.toString());
                            }
                            System.out.println("Introduce la posición que quieres borrar");
                            int posicion = Integer.parseInt(sc.nextLine());
                            System.out.println("Introduce la matrícula del coche que quieres borrar");
                            String matricula = sc.nextLine();
                            for (Coche c : cocheList) {
                                if (c.getTuition().equalsIgnoreCase(matricula)) {
                                    System.out.println("Borrando coche...");
                                    cocheList.remove(c);
                                    borrar(posicion, fileName, c);
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Error al borrar el coche");
                        }
                    }
                    case 3 -> {
                        try {
                            Map<String, Integer> datos = new HashMap<>();
                            datos.put("matricula", (int) BYTES_MATRICULA);
                            datos.put("modelo", (int) BYTES_MODELO);
                            datos.put("marca", (int) BYTES_MARCA);
                            existeFichero(fileName);
                            Fichero fichero = new Fichero(fileName, datos, "matricula");
                            leerFichero(fileName);
                            for(Coche c : cocheList){
                                System.out.println(c.toString());
                            }
                            System.out.println("Introduce la posición que quieres modificar");
                            int posicion = Integer.parseInt(sc.nextLine());
                            System.out.println("Introduce la marca");
                            String marca = sc.nextLine();
                            System.out.println("Introduce el modelo");
                            String modelo = sc.nextLine();
                            Coche coche = new Coche(marca, modelo, cocheList.get(posicion).getTuition());
                            modificar(coche, posicion, fileName);

                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case 4 -> {
                        try {
                            existeFichero(fileName);
                            ordenarFichero(fileName);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case 5 -> {
                        try {
                            Map<String, Integer> datos = new HashMap<>();
                            datos.put("matricula", (int) BYTES_MATRICULA);
                            datos.put("modelo", (int) BYTES_MODELO);
                            datos.put("marca", (int) BYTES_MARCA);
                            existeFichero(fileName);
                            System.out.println("Introduce el nombre del fichero donde volcar los datos");
                            String ficheroVolcado = sc.nextLine();
                            Fichero fichero = new Fichero(fileName, datos, "matricula");
                            leerCSV(fileName,ficheroVolcado);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case 6 -> salir = true;
                    default -> throw new IllegalStateException("Unexpected value: " + opcion);
                }
            } catch (NumberFormatException e) {
                System.err.println("La opción debe ser un número");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (!salir);
    }

    /**
     * Método que muestra el menú de la aplicación.
     * @return String con el menú.
     */
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
