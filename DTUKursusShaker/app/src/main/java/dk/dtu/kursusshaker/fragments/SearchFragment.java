package dk.dtu.kursusshaker.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dk.dtu.kursusshaker.R;
import dk.dtu.kursusshaker.activities.ViewCourseActivity;
import dk.dtu.kursusshaker.data.Course;
import dk.dtu.kursusshaker.data.CoursesAsObject;
import dk.dtu.kursusshaker.data.OnBoardingViewModel;
import dk.dtu.kursusshaker.data.PrimaryViewModel;


public class SearchFragment extends Fragment {

    private static final String WHAT_FRAGMENT_HOST = "WHAT_FRAGMENT_HOST";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SOURCE = "param1";
    private static final String ACTION = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public View view;
    public SimpleAdapter simpleAdapter;

    private OnFragmentInteractionListener mListener;
    public ListView listView;

    OnBoardingViewModel onBoardingViewModel;

    CoursesAsObject coursesAsObject;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(SOURCE);
            mParam2 = getArguments().getString(ACTION);
        }
    }

    private ArrayList getFilteredCourses() {
        // Returns a filtered array of courses
        Course[] allCourses = coursesAsObject.getCourseArray();
        ArrayList<Course> filteredCourses = new ArrayList<>(Arrays.asList(allCourses));

        // TODO: Get these from shared preferences
        String[] scheduleFilter = {"E2A","E","F2B","Januar"};
        String[] completed = {"02131","02139","02115","01005","02102"};
        String[] teachingLanguages = {"en-GB","da-DK"};
        String[] locations = {"Campus_Lyngby"};


        for (Course course : allCourses) {
            if (Arrays.asList(completed).contains(course.getCourseCode()) ||
                    !scheduleMeetsPreferences(scheduleFilter, course.getSchedule()) ||
                    !allPrerequisitesAreMet(completed, course) ||
                    prerequisiteIsMet(completed, course.getNoCreditPointsWith()) ||
                    !filterLanguage(teachingLanguages,course) ||
                    !filterLocation(locations,course))
            {
                filteredCourses.remove(course);
            }
        }

        return filteredCourses;
    }

    private boolean filterLocation(String[] locations, Course course) {
        return Arrays.asList(locations).contains(course.getLocation());
    }

    private boolean filterLanguage(String[] languages, Course course) {
        return Arrays.asList(languages).contains(course.getTeachingLanguage());
    }

    // Checks if at least one course in each prerequisite arrays is fulfilled
    private boolean allPrerequisitesAreMet(String[] completedCourses, Course course) {
        String[][] mandatoryPrerequisites = course.getMandatoryPrerequisites();
        String[][] qualifiedPrerequisites = course.getQualifiedPrerequisites();

        for (String[] prerequisite : qualifiedPrerequisites) {
            if (!prerequisiteIsMet(completedCourses, prerequisite)) return false;
        }

        for (String[] prerequisite : mandatoryPrerequisites) {
            if (!prerequisiteIsMet(completedCourses, prerequisite)) return false;
        }

        return true;
    }

    private String[] getAliases(Course course) {
        String[] noPointsWith = course.getNoCreditPointsWith();
        String[] previousNames = course.getPreviousCourses();

        // Collect all equivalent courses
        Set<String> set = new HashSet<>(Collections.singleton(course.getCourseCode()));
        if (noPointsWith != null) set.addAll(Arrays.asList(noPointsWith));
        if (previousNames != null) set.addAll(Arrays.asList(previousNames));

        return set.toArray(new String[0]);
    }

    // Checks if at least one value of completedCourses is also present in courseArray
    // TODO: samarbejde ml. prerequisites og aliases, se 02155's krav
    private boolean prerequisiteIsMet(String[] completedCourses, String[] courseArray) {
        for (String course : courseArray) {
            String[] aliases = getAliases(coursesAsObject.getCourseFromId(course));

            for (String string : aliases) {

                if (Arrays.asList(completedCourses).contains(string)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Checks if a course's schedule fits with the given filter
    private boolean scheduleMeetsPreferences(String[] scheduleFilter, String[][] courseSchedule) {
        if (courseSchedule.length == 0) return true;
        if (scheduleFilter.length == 0) return true;

        for (String[] schedules : courseSchedule) {
            if (Arrays.asList(scheduleFilter).containsAll(Arrays.asList(schedules))) return true;

        }
        return false;
    }

    private void insertCoursesInListView() throws IOException { //TODO skal laves til MVC

        coursesAsObject = new CoursesAsObject(getContext());
        final ArrayList<Course> courseArray = getFilteredCourses();

        String[] courseNames = new String[courseArray.size()];
        String[] courseIds = new String[courseArray.size()];

        int j = 0;
        for (Course currentCourse : courseArray) {
            courseNames[j] = currentCourse.getDanishTitle();
            courseIds[j] = currentCourse.getCourseCode();
            j++;
        }

        ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();

        int titleLen = courseNames.length;
        for (int i = 0; i < titleLen; i++) {
            Map<String, Object> listItemMap = new HashMap<String, Object>();
            listItemMap.put("title", courseNames[i]);
            listItemMap.put("description", courseIds[i]);
            itemDataList.add(listItemMap);
        }

        simpleAdapter = new SimpleAdapter(getActivity(), itemDataList, android.R.layout.simple_list_item_2,
                new String[]{"title", "description"}, new int[]{android.R.id.text1, android.R.id.text2});

        listView = (ListView) view.findViewById(R.id.list_item_all_courses);
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(onItemClickListener);

        TextInputEditText courseFilter = (TextInputEditText) view.findViewById(R.id.search_all_courses_filter);

        courseFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                simpleAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // Listener that chooses the behavior when user clicks an Item in the list
    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
            HashMap clickItemObj = (HashMap) adapterView.getAdapter().getItem(index);
            String selectedCourseCode = (String) clickItemObj.get("description");
            Course intentCourse = coursesAsObject.getCourseFromId(selectedCourseCode);

            // Let the listView behave differently on userInput based on application state
            if (onBoardingViewModel.getOnBoardingInProgress()) {
                if (onBoardingViewModel.addFinishedCourseToArrayList(intentCourse)) {
                    onBoardingViewModel.callViewModel();
                    Toast toast = Toast.makeText(getContext(), "Course: " + intentCourse.getCourseCode() + " added", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Course already added!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

            } else {
                // Following code is run when in primaryActivity aka. when onBoarding is done
                // the intent goes from PrimaryActivity to ViewCourseActivity
                // We make an intent with result in case the user adds the course to his/her basket
                // and then the result course is added to the PrimaryViewModel so the basketFragment can interact with it!
                Intent intent = new Intent(getContext(), ViewCourseActivity.class);
                intent.putExtra("selectedCourse", intentCourse);
                startActivityForResult(intent, 1);
            }
        }
    };


    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(WHAT_FRAGMENT_HOST, "SearchFragment is attached to: " + getActivity());

        // Link SearchFragment to the OnBoardingViewModel
        onBoardingViewModel = ViewModelProviders.of(this.getActivity()).get(OnBoardingViewModel.class);

        // Link SearchFragment to the PrimaryViewModel
        //primaryViewModel = ViewModelProviders.of(this.getActivity()).get(PrimaryViewModel.class);

        view = inflater.inflate(R.layout.fragment_search, container, false);
        try {
            this.insertCoursesInListView();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e(WHAT_FRAGMENT_HOST, "SearchFragment is attached to: " + getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void sayHello() {
        Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
