package com.example.budspaces.Views.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budspaces.Enums.WarnType;
import com.example.budspaces.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WarnActivity extends AppCompatActivity {
    @BindView(R.id.question)
    TextView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String name = " ‘" + getIntent().getStringExtra("name") + "‘ ?";
        String action = getIntent().getStringExtra("action");
        String type = getIntent().getStringExtra("type");

        assert type != null;
        assert action != null;
        if (type.equals(WarnType.group.name())) {
            if (action.equals(WarnType.leave.name())) {
                question.setText(getText(R.string.qst_leave_group) + name);
            } else if (action.equals(WarnType.delete.name())) {
                question.setText(getText(R.string.qst_del_group) + name);
            }
        } else if (type.equals(WarnType.event.name())) {
            if (action.equals(WarnType.leave.name())) {
                question.setText(getText(R.string.qst_leave_event) + name);
            } else if (action.equals(WarnType.delete.name())) {
                question.setText(getText(R.string.qst_del_event) + name);
            }
        }
    }

    private void setChoice(boolean choice) {
        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putBoolean("choice", choice);
        intent.putExtras(bundle);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.deny, R.id.approve})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.deny:
                setChoice(false);
                break;
            case R.id.approve:
                setChoice(true);
                break;
        }
    }
}