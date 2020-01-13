/* Copyright 2016 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeapex.simplrpostprod.opencodelocation.code;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.opencodelocation.localities.Locality;
import com.codeapex.simplrpostprod.opencodelocation.search.SearchContract;
import com.google.openlocationcode.OpenLocationCode;


public class CodeView extends LinearLayout implements CodeContract.View, SearchContract.TargetView {

    public static TextView mCodeTV;
    private final Resources resources;
    private OpenLocationCode lastFullCode;
    private boolean localityValid = false;

    public CodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.code, this, true);
        resources = context.getResources();

        mCodeTV =  findViewById(R.id.code);

    }

    public String getCodeAndLocality() {
        if (!localityValid) {
            return lastFullCode.getCode();
        }
        return "";
    }

    public OpenLocationCode getLastFullCode() {
        return lastFullCode;
    }

    @Override
    public void displayCode(OpenLocationCode code) {
        lastFullCode = code;
        // Display the code but remove the first four digits.
        Log.e("FULL PLUS CODE",""+code.getCode().substring(0, code.getCode().length() - 1));
        mCodeTV.setText(code.getCode().substring(0, code.getCode().length() - 1));
        // Try to append a locality. If we don't have one, get the unknown string.

    }

    @Override
    public void showSearchCode(OpenLocationCode code) {
        displayCode(code);
    }
}
