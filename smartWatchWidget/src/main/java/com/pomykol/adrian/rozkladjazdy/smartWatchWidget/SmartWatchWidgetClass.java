/*
 Copyright (c) 2011, Sony Ericsson Mobile Communications AB
 Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB nor the names
 of its contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.pomykol.adrian.rozkladjazdy.smartWatchWidget;

/**
 * Created by adrian on 21.11.15.
 */

import java.util.ArrayList;
import java.util.List;

public class SmartWatchWidgetClass {
    private String line;
    private String currentBusStop;
    private String firstBusStop;
    private String lastBusStop;
    private String selectedDate;
    private List<String> timesList = new ArrayList<String>();

    public void setLine(String line){ this.line = line; }
    public String getLine(){ return line; }

    public void setCurrentBusStop(String currentBusStop){ this.currentBusStop = currentBusStop; }
    public String getCurrentBusStop(){ return currentBusStop; }

    public void setFirstBusStop(String firstBusStop){ this.firstBusStop = firstBusStop; }
    public String getFirstBusStop(){ return firstBusStop; }

    public void setLastBusStop(String lastBusStop){ this.lastBusStop = lastBusStop; }
    public String getLastBusStop(){ return lastBusStop; }

    public void setTimesList(List<String> timesList){ this.timesList = timesList; }
    public List<String> getTimesList(){ return timesList; }

    public void setSelectedDate(String selectedDate) { this.selectedDate = selectedDate;}
    public String getSelectedDate() { return selectedDate; }

}
