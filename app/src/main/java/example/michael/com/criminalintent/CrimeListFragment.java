package example.michael.com.criminalintent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private Crime mClickedCrime;

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mClickedCrime = null;
        updateUI();
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;
        private CrimeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }
        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd\tE\ta\tHH:mm:ss",Locale.CHINA);
            mDateTextView.setText(df.format(mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved()?View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            mClickedCrime = mCrime;
            startActivity(CrimePageActivity.newIntent(getActivity(),mCrime.getId()));
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;
        private CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(inflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            crime.setItemPosition(position);
            holder.bind(crime);

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    private void updateUI(){
        List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();
        if(mAdapter == null) {
            mAdapter  = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else {
            if(mClickedCrime == null){
                mAdapter.notifyDataSetChanged();
            }else {
                mAdapter.notifyItemChanged(mClickedCrime.getItemPosition());
            }
        }
    }
}
