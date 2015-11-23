package jcsobrino.tddm.uoc.sharetool.service.impl;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
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
import jcsobrino.tddm.uoc.sharetool.domain.Tool;
import jcsobrino.tddm.uoc.sharetool.domain.User;
import jcsobrino.tddm.uoc.sharetool.dto.ITool;
import jcsobrino.tddm.uoc.sharetool.dto.IUser;
import jcsobrino.tddm.uoc.sharetool.service.ApiService;

/**
 * Created by JoséCarlos on 13/11/2015.
 */
public class ApiServiceImpl implements ApiService {

    private static final Integer MIN_DELAY_MILLISECONDS = 1000;
    private static final Integer MAX_DELAY_MILLISECONDS = 2000;

    public ApiServiceImpl() {

        //SQLiteDatabase db = SQLiteDatabase.deleteDatabase("ShareTool.db");
        //populateDatabase();
    }

    private void populateDatabase() {

        int c = new Select().from(User.class).count();


        User user1 = new User("Nombre usuario 1", "1@1.com", "password1");
        user1.save();
        User user2 = new User("Nombre usuario 2", "2@2.com", "password2");
        user2.save();

        List<User> listUsers = Arrays.asList(user1, user2);

        float priceMin = 50.0f;
        float priceMax = 100.0f;

        float latMax = 41.476175f;
        float latMin = 41.311855f;
        float lngMax = 2.278976f;
        float lngMin = 2.021141f;

        Random random = new Random();

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < 10; i++) {

                float pricePerDay = random.nextFloat() * (priceMax - priceMin) + priceMin;
                float lat = random.nextFloat() * (latMax - latMin) + latMin;
                float lng = random.nextFloat() * (lngMax - lngMin) + lngMin;

                User userTool = listUsers.get(random.nextInt(listUsers.size()));
                Tool tool = new Tool("Tool name " + i, "Tool description " + i, pricePerDay, userTool, lat, lng);
                tool.save();
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        int count = new Select().from(Tool.class).count();

        count++;
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

        if (lat != null && lng != null) {

            Iterator<Tool> it = listTools.iterator();

            while (it.hasNext()) {

                Tool tool = it.next();
                tool.setDistanceInKilometers(calculateDistanceKilometers(tool, lat, lng));

                if (maxKilometers != null && tool.getDistanceInKilometers() > maxKilometers) {
                    it.remove();
                }
            }

        }

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


    private void simulateDelay() {

        try {
            Thread.sleep(UtilFunctions.randomBetween(MIN_DELAY_MILLISECONDS, MAX_DELAY_MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Float calculateDistanceKilometers(final Tool tool, final Float lat, final Float lng) {

        Double factor = 57.29577951308232087679815481410517033235;
        Double lat_rad = lat / factor;
        Double lng_rad = lng / factor;
        Double f_lat_rad = tool.getPositionLat() / factor;
        Double f_lng_rad = tool.getPositionLng() / factor;

        return new Float(6371 * Math.acos(Math.cos(f_lat_rad) * Math.cos(lat_rad) * Math.cos(lng_rad - f_lng_rad) + Math.sin(f_lat_rad) * Math.sin(lat_rad)));
    }
}
