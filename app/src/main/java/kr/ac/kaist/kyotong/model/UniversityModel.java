package kr.ac.kaist.kyotong.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by yearnning on 15. 1. 22..
 */
public class UniversityModel {

    public int id = -1;
    public String name = "";
    public ArrayList<ShuttleModel> shuttleModels = new ArrayList<>();

    public static UniversityModel newInstance(Context context, int id) {

        if (id == 1) {
            return newInstance(1, "KAIST")
                    .addShuttleModel(ShuttleModel.newInstance(context, 1))
                    .addShuttleModel(ShuttleModel.newInstance(context, 2))
                    .addShuttleModel(ShuttleModel.newInstance(context, 3));
        } else {
            return null;
        }
    }

    private static UniversityModel newInstance(int id, String name) {
        UniversityModel universityModel = new UniversityModel();
        universityModel.id = id;
        universityModel.name = name;
        return universityModel;
    }

    /**
     *
     */
    private UniversityModel addShuttleModel(ShuttleModel shuttleModel) {

        int i;
        for (i = 0; i < shuttleModels.size(); i++) {
            if (shuttleModels.get(i).weight < shuttleModel.weight) {
                break;
            }
        }

        this.shuttleModels.add(i, shuttleModel);
        return this;
    }


}
