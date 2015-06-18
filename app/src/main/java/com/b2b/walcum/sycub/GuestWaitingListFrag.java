package com.b2b.walcum.sycub;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.b2b.walcum.sycub.dummy.DummyContent;
import com.b2b.walcum.sycub.model.GuestWaiting;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A fragment representing a list of Items.
 * <p>
 * <p>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GuestWaitingListFrag extends ListFragment {

    private OnFragmentInteractionListener mListener;
    Realm realm;
    private MyListAdapter mAdapter;
    private ListView mListView;
    private ArrayList<GuestWaiting> guests = new ArrayList<>();



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GuestWaitingListFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                         Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        View v = inflater.inflate(R.layout.frag_list_view, container, false);
        return v;
    }

   @Override
    public void onActivityCreated(Bundle savedInstanceState){

        super.onActivityCreated(savedInstanceState);

        realm = Realm.getInstance(getActivity());
        RealmResults<GuestWaiting> results = realm.where(GuestWaiting.class).findAll();

       //Adding header in list view
       View headerView = getActivity().getLayoutInflater().inflate(R.layout.header, null);
       setListAdapter(null);
       getListView().addHeaderView(headerView);

        mAdapter = new MyListAdapter(results, getActivity());
        setListAdapter(mAdapter);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        realm.close();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
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
        public void onFragmentInteraction(String id);
    }

}
