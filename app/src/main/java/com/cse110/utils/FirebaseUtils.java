package com.cse110.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.cse110.eventlit.CardFragment;
import com.cse110.eventlit.OrganizationsAdapter;
import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A utility class to get data related to student organizations
 * Created by: Sandeep
 */

public class FirebaseUtils {
    // Get a reference to the Firebase Database
    private static DatabaseReference fbDBRef = FirebaseDatabase.getInstance().getReference();

    /**
     * Fetch a list of student organizations at UCSD, save it off in an ArrayAdapter,
     * Notify the ArrayAdapter of the change.
     *
     */
    public static void getAllStudentOrganizations(final OrganizationsAdapter adapter,
                                                  final ArrayList<Organization> orgsList){
        final DatabaseReference organizations = fbDBRef.child("organizations");
        ValueEventListener postListener = new ValueEventListener() {
            // Get a snapshot of the database organizations document
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reindex and add new organziations
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    String orgKey = shot.getKey();
                    String orgName = shot.getValue().toString();
                    Organization org = new Organization(orgKey, orgName);

                    // Add the new org to the list and notify the adapter.
                    orgsList.add(org);
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
                for (int i = 0; i < orgsList.size(); i++){
                    Log.w("Organization " + i + ":",  orgsList.get(i).toString());
                }
                adapter.notifyDataSetChanged();
            }

            // Elegantly handle the error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("OrganizerUtils", "Could not retrieve student organizations");
            }
        };
        organizations.addListenerForSingleValueEvent(postListener);
    }

    /**
     * Fetch a list of events and save it off in an ArrayAdapter. Notify the ArrayAdapter of the
     * change.
     */
    public static void getEventsByOrgId(final CardFragment.MyAdapter adapter,
                                        final ArrayList<Event> adapterArray,
                                        final String orgId){
        final DatabaseReference events = fbDBRef.child("events").child(orgId);
        ValueEventListener eventListener = new ValueEventListener() {
            // Get a snapshot of events db
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    if (eventSnapshot.hasChild("orgid")){
                        String eventOrgId = eventSnapshot.child("orgid").getValue().toString();
                        if (eventOrgId.equals(orgId)){

                            // Get all fields in an event object
                            String title = eventSnapshot.child("title").getValue()
                                           .toString();
                            String category = eventSnapshot.child("category").getValue().toString();
                            String description = eventSnapshot.child("description").getValue()
                                                 .toString();

                            Calendar startDate = new GregorianCalendar();
                            String startMilli = eventSnapshot.child("startDate").getValue()
                                                .toString();
                            startDate.setTimeInMillis(Long.parseLong(startMilli));

                            Calendar endDate = new GregorianCalendar();
                            String endMilli = eventSnapshot.child("endDate").getValue().toString();
                            endDate.setTimeInMillis(Long.parseLong(endMilli));

                            String location = eventSnapshot.child("location").getValue().toString();
                            int maxCapacity = Integer
                                    .parseInt(eventSnapshot.child("maxCapacity")
                                            .getValue().toString());

                            // Create an event object and add it to the adapter
                            Event event = new Event(title, description, orgId, startDate,
                                    endDate,location, category, maxCapacity);
                            adapterArray.add(event);
                            adapter.notifyItemChanged(adapter.getItemCount() - 1);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseUtils", "Could not retrieve events");
            }
        };
        events.addValueEventListener(eventListener);
    }

    /**
     * Fetch a list of all events regardless of organization.
     *
     * @param adapter - the adapter to notify once the ArrayList has been populated
     * @param eventList - an ArrayList of events to be populated
     */
    public static void getAllEvents(final CardFragment.MyAdapter adapter,
                                    final ArrayList<Event> eventList) {
        final DatabaseReference events = fbDBRef.child("events");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Enumerate through all the organizations
                for (DataSnapshot org : dataSnapshot.getChildren()){
                    // Makes call to other method to get events for the org
                    getEventsByOrgId(adapter, eventList, org.getKey());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseUtils", "Could not retrieve events");
            }
        };
        events.addValueEventListener(eventListener);

    }
}