package com.example.musicplayerfrontend;

import android.app.Dialog;
import android.content.Context;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class EqualizerDialog extends Dialog {
    private Equalizer equalizer;
    private final int sessionId;

    public EqualizerDialog(Context ctx, int sessionId) {
        super(ctx);
        this.sessionId = sessionId;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        equalizer = new Equalizer(0, sessionId);
        equalizer.setEnabled(true);

        final short bands = equalizer.getNumberOfBands();
        final short minEQ = equalizer.getBandLevelRange()[0];
        final short maxEQ = equalizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView freq = new TextView(getContext());
            freq.setText((equalizer.getCenterFreq(i) / 1000) + " Hz");
            row.addView(freq);

            SeekBar bar = new SeekBar(getContext());
            bar.setMax(maxEQ - minEQ);
            bar.setProgress(equalizer.getBandLevel(i) - minEQ);
            short bandFinal = i;
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                // PUBLIC_INTERFACE
                @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    equalizer.setBandLevel(bandFinal, (short) (progress + minEQ));
                }
                // PUBLIC_INTERFACE
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                // PUBLIC_INTERFACE
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            row.addView(bar);
            layout.addView(row);
        }
        setContentView(layout);
    }
}
