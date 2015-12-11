package jcsobrino.tddm.uoc.sharetool.service.impl;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jcsobrino.tddm.uoc.sharetool.common.ToolOrderEnum;
import jcsobrino.tddm.uoc.sharetool.common.UtilFunctions;
import jcsobrino.tddm.uoc.sharetool.domain.impl.Tool;
import jcsobrino.tddm.uoc.sharetool.domain.impl.User;
import jcsobrino.tddm.uoc.sharetool.domain.ITool;
import jcsobrino.tddm.uoc.sharetool.domain.IUser;
import jcsobrino.tddm.uoc.sharetool.service.ApiService;

/**
 * Implementación de la API sobre SQLite
 * Created by JoséCarlos on 13/11/2015.
 */
public class ApiServiceImpl implements ApiService {

    // tiempo de simulación de retardo en las llamadas a la API
    private static final Integer MIN_DELAY_MILLISECONDS = 1000;
    private static final Integer MAX_DELAY_MILLISECONDS = 2000;

    public ApiServiceImpl() {

        populateDatabase();
    }

    private void populateDatabase() {

        // borrar los registros de las tablas de herramientas y usuarios
        new Delete().from(Tool.class).execute();
        new Delete().from(User.class).execute();

        // parámetros para la generación de datos de prueba
        final int MAX_TOOLS = 50;
        final float PRICE_MIN = 1.0f;
        final float PRICE_MAX = 100.0f;
        final float LAT_MAX = 41.47f;
        final float LAT_MIN = 41.31f;
        final float LNG_MAX = 2.27f;
        final float LNG_MIN = 2.02f;

        final String[] toolNames = {
                "Taladro Percutor 500W",
                "Mini sierra circular",
                "Cepillo de carpintero",
                "Maletín de herramientas neumáticas",
                "Mesa de trabajo para taller bricolaje",
                "Taladro sin cable, cabezal extraíble",
                "Juego de llaves hexagonales",
                "Pie de cabra 340mm",
                "Banco de trabajo plegable",
                "Crimpadora (24,1 cm)",
                "Medidor láser/detector",
                "Sierra circular, 1200 W, 185 mm",
                "Atornillador angular de 90°",
                "Pinzas de bricolaje",
                "Kit de puntas y brocas para bricolaje",
                "Fresas de muelle",
                "Juego de brocas de impacto",
                "Amoladora angular",
                "Taladradora de percusión",
                "Sierra de calar de carrera pendular",
                "Pistola de pegar",
                "Atornillador a batería de litio",
                "Aspirador en seco-húmedo",
                "Taladro inalámbrico, 12 V",
                "Sierra de vaivén con maletín",
                "Afiladora doble, 150 W",
                "Martillo perforador, 36 V"};

        final String descriptionTool = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec et odio id libero condimentum dapibus non non neque. Sed congue auctor nibh, eget congue arcu vehicula in. Suspendisse potenti. Integer ac neque est. Donec sit amet aliquam nisl, vitae convallis leo. Vivamus vitae neque libero. Sed sodales hendrerit massa, eget dictum nibh molestie at.";

        //alta usuarios de prueba
        User user1 = new User("Wayne Holmes", "wholmes@host.com", "password1");
        user1.save();
        User user2 = new User("Tammy Harris", "tharris@host.com", "password2");
        user2.save();
        User user3 = new User("Mario Brady", "mbrady@host.com", "password3");
        user3.save();

        List<User> listUsers = Arrays.asList(user1, user2, user3);

        // alta herramientas de prueba
        Random random = new Random();
        for (int i = 0; i < MAX_TOOLS; i++) {

            float pricePerDay = UtilFunctions.randomBetween(PRICE_MIN, PRICE_MAX);
            float lat = UtilFunctions.randomBetween(LAT_MIN, LAT_MAX);
            float lng = UtilFunctions.randomBetween(LNG_MIN, LNG_MAX);

            User userTool = listUsers.get(random.nextInt(listUsers.size()));
            Tool tool = new Tool(toolNames[i % toolNames.length], descriptionTool, pricePerDay, userTool, lat, lng);
            tool.save();
        }
    }

    @Override
    public IUser login(final String email, final String password) {
        simulateDelay();
        return new Select().from(User.class).where("email = ?", email).and("password = ?", password).executeSingle();
    }

    @Override
    public boolean userExistsByEmail(final String email) {
        return new Select().from(User.class).where("email = ?", email).exists();
    }

    @Override
    public IUser createUser(final String name, final String email, final String password) {
        simulateDelay();

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);

        newUser.save();

        return newUser;
    }

    @Override
    public List<? extends ITool> findTools(final String name, final Float maxPrice, final Float maxKilometers, final Float lat, final Float lng, final ToolOrderEnum toolOrder) {
        simulateDelay();
        From fromTool = new Select().from(Tool.class);

        if (!TextUtils.isEmpty(name)) {
            fromTool.and("UPPER(name) like ?", String.format("%%%s%%", name.toUpperCase()));
        }

        if (maxPrice != null) {
            fromTool.and("pricePerDay <= ?", maxPrice);
        }

        if (toolOrder != null) {
            fromTool.orderBy(toolOrder.getValue());
        }

        List<Tool> listTools = fromTool.execute();

        // SQLite para Android no permite la creación de funciones por lo que no es posible
        // calcular la distancia de las herramientas directamente sobre la base de datos
        // Por esta razón, esta operación hay que hacerla sobre Java
        if (lat != null && lng != null) {

            float[] distance = new float[3];
            Iterator<Tool> it = listTools.iterator();

            while (it.hasNext()) {

                Tool tool = it.next();

                tool.setDistanceInKilometers(UtilFunctions.calculateDistance(tool, lat, lng));

                // si el usuario indica filtro de distanca máxima, se eliminan las herramientas afectadas
                if (maxKilometers != null && tool.getDistanceInKilometers() > maxKilometers) {
                    it.remove();
                }
            }
        } else {

            // supuesto bug de ActiveAndroid: los campos no almacenados mantienen su valor entre invocaciones a la bbdd
            for (Tool t:listTools) {
                t.setDistanceInKilometers(null);
            }
        }

        // ordenación por herramienta más cercana. No se puede hacer directamente sobre SQLite
        if (toolOrder == ToolOrderEnum.NEAR_TOOL && lat != null && lng != null) {

            Collections.sort(listTools, new Comparator<Tool>() {
                @Override
                public int compare(Tool lhs, Tool rhs) {
                    return lhs.getDistanceInKilometers().compareTo(rhs.getDistanceInKilometers());
                }
            });
        }

        return listTools;
    }

    @Override
    public ITool getToolById(Integer id) {
        simulateDelay();
        return new Select().from(Tool.class).where("id = ?", id).executeSingle();
    }

    @Override
    public IUser getUserById(Integer id) {
        simulateDelay();
        return new Select().from(User.class).where("id = ?", id).executeSingle();
    }

    // simula un retardo en la ejecución de las funciones a modo de llamadas a través de Internet
    private void simulateDelay() {

        try {
            Thread.sleep(UtilFunctions.randomBetween(MIN_DELAY_MILLISECONDS, MAX_DELAY_MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
