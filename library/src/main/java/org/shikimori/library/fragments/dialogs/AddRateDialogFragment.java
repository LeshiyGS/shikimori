package org.shikimori.library.fragments.dialogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.UserRate;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.hs;

import ru.altarix.ui.CustomEditText;
import ru.altarix.ui.CustomSpinner;

/**
 * Created by Феофилактов on 08.07.2015.
 */
public class AddRateDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    private LayoutInflater inflater;
    private ProjectTool.TYPE type;

    CustomSpinner csRating, csStatus;
    CustomEditText cetComment, cetRewatching, cetEpisodes;
    private ControllListenerRate listener;
    private View bMinus, bPlus, bSave;
    private int maxEpisodes;

    public static AddRateDialogFragment newInstance() {
        return new AddRateDialogFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View v = inflater.inflate(R.layout.dialog_fragment_add_rate, null);

        csRating = hs.get(v, R.id.csRating);
        csStatus = hs.get(v, R.id.csStatus);
        cetComment = hs.get(v, R.id.cetComment);
        cetRewatching = hs.get(v, R.id.cetRewatching);
        cetEpisodes = hs.get(v, R.id.cetEpisodes);
        bMinus = hs.get(v, R.id.bMinus);
        bPlus = hs.get(v, R.id.bPlus);
        bSave = hs.get(v, R.id.bSave);

        bMinus.setOnClickListener(this);
        bPlus.setOnClickListener(this);
        bSave.setOnClickListener(this);
        cetEpisodes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(maxEpisodes > 0){
                    if(s.length() == 0)
                        cetEpisodes.setText("0");
                    else{
                        int epizodes = Integer.valueOf(s.toString());
                        if(maxEpisodes < epizodes)
                            cetEpisodes.setText(String.valueOf(maxEpisodes));
                    }
                }

            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (type == ProjectTool.TYPE.ANIME)
            csStatus.setList(getActivity().getResources().getStringArray(R.array.status_anime));
        else
            csStatus.setList(getActivity().getResources().getStringArray(R.array.status_manga));

        if (listener != null) {
            UserRate rate = listener.getRateUser();
            csStatus.setSelection(ProjectTool.getListPositionFromStatus(rate.status));
            csRating.setSelection(rate.score);
            cetComment.setText(rate.text);
            cetRewatching.setText(String.valueOf(rate.rewatches));
            cetEpisodes.setText(String.valueOf(rate.episodes));
        }

    }

    public void setType(ProjectTool.TYPE type) {
        this.type = type;
    }

    public void setUpdateListener(ControllListenerRate listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bSave) {
            fillRate();
            listener.updateRateFromDialog();
            dismiss();
        } else if (id == R.id.bMinus) {
            increaseEpisode(false);
        } else if (id == R.id.bPlus) {
            increaseEpisode(true);
        }
    }

    /**
     * Update object rate
     */
    private void fillRate() {
        UserRate rate = listener.getRateUser();
        rate.status = ProjectTool.getListStatusFromPosition(csStatus.getSelectedItemPosition());
        rate.statusInt = UserRate.Status.fromStatus(rate.status);
        rate.score = csRating.getSelectedItemPosition();
        rate.text = cetComment.getText();
        // count rewatch
        rate.rewatches = getIntValue(cetRewatching.getText());
        // episodes
        rate.episodes = getIntValue(cetEpisodes.getText());
    }

    private int getIntValue(String text) {
        int val = 0;
        if (!TextUtils.isEmpty(text))
            val = Integer.parseInt(text);
        return val;
    }

    /**
     * Up or down number episode
     *
     * @param increase
     */
    private void increaseEpisode(boolean increase) {
        String episod = cetEpisodes.getText();
        int episodInt = 0;
        if (!TextUtils.isEmpty(episod)) {
            episodInt = Integer.valueOf(episod);
            if (increase){
                if(maxEpisodes>0 && maxEpisodes <= episodInt)
                    return;
                episodInt++;

            } else
                episodInt--;

            if (episodInt < 0)
                episodInt = 0;
        }

        cetEpisodes.setText(String.valueOf(episodInt));
    }

    public void setMaxEpisodes(int maxEpisodes) {
        this.maxEpisodes = maxEpisodes;
    }

    public interface ControllListenerRate {
        public UserRate getRateUser();

        public void updateRateFromDialog();
    }
}
