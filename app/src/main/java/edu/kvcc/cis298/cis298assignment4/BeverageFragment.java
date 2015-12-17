//ANDY CULLEN
//ASSIGNMENT 4
//DUE 12/16/15

package edu.kvcc.cis298.cis298assignment4;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.widget.Toast;

/**
 * Created by David Barnes on 11/3/2015.
 */

public class BeverageFragment extends Fragment implements View.OnClickListener {

    //String keys that will be used to send data between fragments
    private static final String ARG_BEVERAGE_ID = "beverage_id";
    private static final int CONTACT_CHOICE_RES = 1234;

    //private class level vars for the model properties
    private EditText mId;
    private EditText mName;
    private EditText mPack;
    private EditText mPrice;

    private CheckBox mActive;

    private Button mSelectContactButton;
    private Button mSendDetailsButton;

    private String mSelectedEmail;
    private String mSelectedName;
    private String mEmailSubject = "Beverage App Item Information";

    //Private var for storing the beverage that will be displayed with this fragment
    private Beverage mBeverage;

    //Public method to get a properly formatted version of this fragment
    public static BeverageFragment newInstance(String id) {
        //Make a bungle for fragment args
        Bundle args = new Bundle();
        //Put the args using the key defined above
        args.putString(ARG_BEVERAGE_ID, id);

        //Make the new fragment, attach the args, and return the fragment
        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //When created, get the beverage id from the fragment args.
        String beverageId = getArguments().getString(ARG_BEVERAGE_ID);
        //use the id to get the beverage from the singleton
        mBeverage = BeverageCollection.get().getBeverage(beverageId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Use the inflator to get the view from the layout
        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        //Get handles to the widget controls in the view
        mId = (EditText) view.findViewById(R.id.beverage_id);
        mName = (EditText) view.findViewById(R.id.beverage_name);
        mPack = (EditText) view.findViewById(R.id.beverage_pack);
        mPrice = (EditText) view.findViewById(R.id.beverage_price);
        mActive = (CheckBox) view.findViewById(R.id.beverage_active);
        mSelectContactButton = (Button) view.findViewById(R.id.select_contact_button);
        mSendDetailsButton = (Button) view.findViewById(R.id.send_beverage_details_button);

        //Set the widgets to the properties of the beverage
        mId.setText(mBeverage.getId());
        mId.setEnabled(false);
        mName.setText(mBeverage.getName());
        mPack.setText(mBeverage.getPack());
        mPrice.setText(Double.toString(mBeverage.getPrice()));
        mActive.setChecked(mBeverage.isActive());

        //Set onclicklisteners for added contact buttons
        mSelectContactButton.setOnClickListener(this);
        mSendDetailsButton.setOnClickListener(this);

        //Text changed listenter for the id. It will not be used since the id will be always be disabled.
        //It can be used later if we want to be able to edit the id.
        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the name. Updates the model as the name is changed
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the Pack. Updates the model as the text is changed
        mPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setPack(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Text listener for the price. Updates the model as the text is typed.
        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the count of characters is greater than 0, we will update the model with the
                //parsed number that is input.
                if (count > 0) {
                    mBeverage.setPrice(Double.parseDouble(s.toString()));
                    //else there is no text in the box and therefore can't be parsed. Just set the price to zero.
                } else {
                    mBeverage.setPrice(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Set a checked changed listener on the checkbox
        mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeverage.setActive(isChecked);
            }
        });

        //Lastly return the view with all of this stuff attached and set on it.
        return view;
    }

    @Override
    public void onClick(View v) {

        //check the button that was clicked and execute the appropriate action
        if (v.getId() == R.id.select_contact_button)
        {
            selectContact();
        } else if (v.getId() == R.id.send_beverage_details_button){
            sendDetails();
        }
    }

    //get the contact
    private void selectContact() {
        Intent contactChoice = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        startActivityForResult(contactChoice, CONTACT_CHOICE_RES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) { //CHECK RESULT CODE
            if (requestCode == CONTACT_CHOICE_RES) {

                //CREATE THE CURSOR THAT ALLOWS NAVIGATION
                Cursor cursor = null;

                String email = "";
                String contactName = "";

                try {
                    Uri result = data.getData();
                    String id = result.getLastPathSegment();

                    cursor = getActivity().getContentResolver().query(Email.CONTENT_URI,
                            null, Email.CONTACT_ID + "=?", new String[] {id}, null);

                    //GET NAME AND EMAIL INDEX FROM THE CURSOR
                    int emailIndex = cursor.getColumnIndex(Email.DATA);
                    int nameIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);

                    //SET THE CURSOR TO FIRST POSITION
                    if (cursor.moveToFirst()) {
                        email = cursor.getString(emailIndex);
                        contactName = cursor.getString(nameIndex);
                    }
                } catch (Exception e) { //CATCH EXCEPTIONS
                    e.printStackTrace();
                } finally {

                    //CLOSE THE CURSOR
                    if (cursor != null) {
                        cursor.close();
                    }

                    //DISPLAY TOAST IF NO EMAIL WAS FOUND
                    if (email.length() == 0) {
                        Toast.makeText(getActivity(), "Contact does not have a recorded email address.", Toast.LENGTH_LONG).show();
                    }

                    //ENABLE SEND DETAILS BUTTON UPON SUCCESS
                    if(email.length() > 1) {
                        mSendDetailsButton.setEnabled(true);
                        mSelectedEmail = email;
                        mSelectedName = contactName;
                    }
                }
            }
        }
    }

    //begin process of sending the details to the contact
    private void sendDetails() {
        String[] addresses = new String[1];
        addresses[0] = mSelectedEmail;
        constructEmail(addresses, mEmailSubject, writeBody());
        mSendDetailsButton.setEnabled(false);
    }

    //write the body of the email
    private String writeBody() {
        String isActiveString;
        if(mBeverage.isActive()) {
            isActiveString = "Currently Active";
        } else {
            isActiveString = "Currently Inactive";
        }

        String emailBody = mSelectedName + "," + System.getProperty("line.separator") + System.getProperty("line.separator") +
                            "Please Review the Following Beverage." + System.getProperty("line.separator") + System.getProperty("line.separator") +
                            mBeverage.getId() + System.getProperty("line.separator") + mBeverage.getName() + System.getProperty("line.separator") +
                            mBeverage.getPack() + System.getProperty("line.separator") + mBeverage.getPrice() + System.getProperty("line.separator") +
                            isActiveString;

        return emailBody;
    }

    //put the parts of the email together (email, subject, body, etc..)
    private void constructEmail(String[] address, String subject, String emailBody) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");

        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
