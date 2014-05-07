/*
 * Copyright 2014 Frakbot (Sebastiano Poggi and Francesco Pontillo)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.blinktextview.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;
import net.frakbot.blinktextview.BlinkTextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BlinkTextView blinkingBastard = (BlinkTextView) findViewById(R.id.what_whaat);
        blinkingBastard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BlinkTextView blinkyWinky = (BlinkTextView) v;
                final boolean annoyPeople = !blinkyWinky.isOcdModeEnabled();
                blinkyWinky.setOcdModeEnabled(annoyPeople);

                final Context context = v.getContext();
                if (context != null) {
                    Toast.makeText(context,
                                   annoyPeople ? getString(R.string.toast_ocd_on) : getString(R.string.toast_ocd_off),
                                   Toast.LENGTH_SHORT)
                         .show();
                }
            }
        });
    }
}
