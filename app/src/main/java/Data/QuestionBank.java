package Data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import controller.AppController;
import model.Question;

public class QuestionBank {

    ArrayList<Question> questionsArrayList = new ArrayList<>();

    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Question> getQuestion(final AnswerListAsynResponse callBack) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, (JSONArray) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for( int i = 0; i < response.length() ; i++ ){
                    try {
                      Question question = new Question();
                      question.setAnswer(response.getJSONArray(i).get(0).toString());
                      question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));
                      questionsArrayList.add(question);
                   }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(null != callBack) callBack.processFinished(questionsArrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              Log.d("Error" , " " + error);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return questionsArrayList;
    }
}
