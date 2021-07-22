package util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void saveHighestScore( int score)
    {
        int lastScore = preferences.getInt("high_score",0);

        if(score > lastScore)
        {
            preferences.edit().putInt("high_score",score).apply();
        }
    }

    public int getHighestScore(){
       return preferences.getInt("high_score",0);
    }

   public void setState(int index){
        preferences.edit().putInt("state",index).apply();
   }

   public int getState(){
       return preferences.getInt("state",0);
   }
}
