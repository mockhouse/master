package com.quick.mockscoop;

import com.quick.base.MockScoopBaseActivity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.quick.checkyourknowledge.ShowResults;
import com.quick.global.Globals;
import com.quick.questions.Question;
import com.quick.questions.QuestionDbHelper;

import java.util.List;


public class LaunchQuiz extends MockScoopBaseActivity {

    public static final int ID_ONE = 1;
    QuestionDbHelper questionDbHelper = null;
    Question[] lstQuestions;
    int[] recordedAnswers;
    long[] startTime;
    long[] duration;
    ViewGroup questionListLayout;
    RadioGroup questionAnswerRadioGroup;
    int nextQuestionIndex = 1;
    TextView header;
    Activity a=null;
    String selectedCategory;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 a=getActivity();
 View rootView = inflater.inflate(R.layout.activity_launch_quiz, container, false);
        return rootView;
    }
   


    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
       super.onActivityCreated(savedInstanceState);
       selectedCategory=getArguments().getString(SelectQuiz.SELECTED_QUIZ);
     

       questionListLayout = (ViewGroup) a.findViewById(R.id.questionList);
       questionAnswerRadioGroup = (RadioGroup) a.findViewById(R.id.questionAnswerOptions);
       header = (TextView) a.findViewById(R.id.textViewHeader);

       questionDbHelper = QuestionDbHelper.getDBHelper(a);
       List<Question> allQuestions = connector.getOrFetchQuestions(selectedCategory, null);//get questions for the category from the cache
       a.findViewById(R.id.btnSkipQuestions).setOnClickListener(skipQuestion);
       a.findViewById(R.id.btnNextQuestion).setOnClickListener(nextQuestion);
       a.findViewById(R.id.btnEndQuiz).setOnClickListener(endQuiz);
       if(allQuestions == null || allQuestions.isEmpty()) {

           header.setText(R.string.noQuestionsAvailable);
     
        
           a.findViewById(R.id.btnNextQuestion).setOnClickListener(nextQuestion);
     
           a.findViewById(R.id.btnEndQuiz).setOnClickListener(endQuiz);
          /* Button btn = (Button) findViewById(R.id.btnEndQuiz);
           btn.setText(getString(R.string.restartQuiz));
           btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(LaunchQuiz.this,SelectQuiz.class);
                   startActivity(intent);
               }
           });*/


           a.findViewById(R.id.btnSkipQuestions).setVisibility(View.INVISIBLE);
           a.findViewById(R.id.btnSkipQuestions).setOnClickListener(skipQuestion);
           return;
          }
       
          lstQuestions = allQuestions.toArray(new Question[allQuestions.size()]);
          startTime = new long[allQuestions.size()];
          duration = new long[allQuestions.size()];

          recordedAnswers = new int[lstQuestions.length];

          header.setText("Questions " + nextQuestionIndex + "/" + lstQuestions.length);

          showQuestion(lstQuestions, questionListLayout, questionAnswerRadioGroup, nextQuestionIndex++);
    }
    
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    OnClickListener skipQuestion=new OnClickListener() {
  
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 showNextQuestion(v,true);
	}};
	  public void endQuiz(View view) {
	       
	    	 //ending the quiz in between as opted by the user
	      
	        //String field = selectedQuiz.getText().toString();
	        Globals.attemptedQuestions = lstQuestions;
	        Globals.recordedAnswers = recordedAnswers;
	        //Globals.userName = "Ronak Patel";
	        Globals.startTime = startTime;
	        Globals.duration = duration;
            Globals.selectedCategory = selectedCategory;
	   	 ShowResults sr = new ShowResults();
    	 Bundle bundle=new Bundle();
    	
 	    android.app.FragmentManager fragmentManager = getFragmentManager();
 	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
 	    fragmentTransaction.replace(R.id.frame_container,sr);
 	   fragmentTransaction.commit();

	    }
	 OnClickListener endQuiz=new OnClickListener() {
  

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 //ending the quiz in between as opted by the user
		 ShowResults sr = new ShowResults();
        //String field = selectedQuiz.getText().toString();
        Globals.attemptedQuestions = lstQuestions;
        Globals.recordedAnswers = recordedAnswers;
        Globals.userName = "Ronak Patel";
        Globals.startTime = startTime;
        Globals.duration = duration;
        android.app.FragmentManager fragmentManager = getFragmentManager();
 	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
 	    fragmentTransaction.replace(R.id.frame_container,sr);
 	   fragmentTransaction.commit();
	}
	 };
	 OnClickListener nextQuestion=new OnClickListener() {
   
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		  showNextQuestion(v,false);
		
	}
	 };
    public void showNextQuestion(View view,boolean skipCurrentQuestion) {


        if(!skipCurrentQuestion) {

            //first record the current answer & then load,show the next question
            recordedAnswers[nextQuestionIndex-2] =  questionAnswerRadioGroup.getCheckedRadioButtonId();
            duration[nextQuestionIndex-2] = System.currentTimeMillis() - startTime[nextQuestionIndex-2];
            System.out.println("Ans " + (nextQuestionIndex - 1) + " : " + questionAnswerRadioGroup.getCheckedRadioButtonId());

        }
        //since the skip flag is on, don't record current question answers

        //remove the previous options , question text would be replaced when next question would be displayed
        questionAnswerRadioGroup.removeAllViews();
        System.out.println("lstQuestions.length :" + lstQuestions.length);
        System.out.println("nextQuestionIndex:" + nextQuestionIndex);
        if (lstQuestions.length < nextQuestionIndex) {
            //now move to the next activity and display the results
            endQuiz(view);
            return;

        }
        header.setText("Questions " + nextQuestionIndex + "/" + lstQuestions.length);
        showQuestion(lstQuestions, questionListLayout, questionAnswerRadioGroup, nextQuestionIndex++);
        Button nextQuestions = (Button) a.findViewById(R.id.btnNextQuestion);
        nextQuestions.setEnabled(false);
    }
	
    private void showQuestion(Question[] questionList, ViewGroup parent, ViewGroup radioGroup, int questionIndex) {
        TextView questionText = null;

        RadioButton radioButton = null;
        int index = 0;
        EnableNextButton enableButton = new EnableNextButton(a);


        Question question = questionList[questionIndex - 1];
        System.out.println("Current Questions : " + question);

        ((TextView) a.findViewById(R.id.questionText)).setText(question.getQuestion());

        radioButton = new RadioButton(a);
        radioButton.setId(++index);
        radioButton.setText(question.getOption1());
        radioButton.setChecked(false);
        radioButton.setOnClickListener(enableButton);
        radioGroup.addView(radioButton);

        radioButton = new RadioButton(a);
        radioButton.setId(++index);
        radioButton.setText(question.getOption2());
        radioButton.setChecked(false);
        radioButton.setOnClickListener(enableButton);
        radioGroup.addView(radioButton);

        radioButton = new RadioButton(a);
        radioButton.setId(++index);
        radioButton.setText(question.getOption3());
        radioButton.setChecked(false);
        radioButton.setOnClickListener(enableButton);
        radioGroup.addView(radioButton);

        radioButton = new RadioButton(a);
        radioButton.setId(++index);
        radioButton.setText(question.getOption4());
        radioButton.setChecked(false);
        radioButton.setOnClickListener(enableButton);
        radioGroup.addView(radioButton);


        //start the timers
        startTime[questionIndex-1] = System.currentTimeMillis();

    }


    private static class EnableNextButton implements View.OnClickListener {
        private Activity activity;

        public EnableNextButton(Activity activity) {
            this.activity = activity;
        }

        public void onClick(View v) {
            Button nextQuestions = (Button) activity.findViewById(R.id.btnNextQuestion);
            nextQuestions.setEnabled(true);
        }
    }

    private void generateRadioButtons(List<Question> values, ViewGroup parent) {

        TextView questionText = null;
        RadioGroup radioGroup = null;
        RadioButton radioButton = null;
        int index = 0;
        for (Question question : values) {

            questionText = new TextView(a);
            questionText.setText(question.getQuestion());

            radioGroup = new RadioGroup(a);

            radioButton = new RadioButton(a);
            radioButton.setId(++index);
            radioButton.setText(question.getOption1());
            radioButton.setChecked(false);

            radioGroup.addView(radioButton);

            radioButton = new RadioButton(a);
            radioButton.setId(++index);
            radioButton.setText(question.getOption2());
            radioButton.setChecked(false);

            radioGroup.addView(radioButton);
            radioButton = new RadioButton(a);
            radioButton.setId(++index);
            radioButton.setText(question.getOption3());
            radioButton.setChecked(false);

            radioGroup.addView(radioButton);
            radioButton = new RadioButton(a);
            radioButton.setId(++index);
            radioButton.setText(question.getOption4());
            radioButton.setChecked(false);

            radioGroup.addView(radioButton);

            parent.addView(questionText);
            parent.addView(radioGroup);
        }
    }
}
