package Data;

import java.util.ArrayList;

import model.Question;

public interface AnswerListAsynResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}
