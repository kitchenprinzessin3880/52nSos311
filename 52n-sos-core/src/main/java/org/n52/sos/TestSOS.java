/***************************************************************
 Copyright (C) 2008
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under 
 the terms of the GNU General Public License version 2 as published by the 
 Free Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program (see gnu-gpl v2.txt). If not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 visit the Free Software Foundation web page, http://www.fsf.org.

 Author: <LIST OF AUTHORS/EDITORS>
 Created: <CREATION DATE>
 Modified: <DATE OF LAST MODIFICATION (optional line)>
***************************************************************/

package org.n52.sos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.n52.sos.resp.ISosResponse;

/**
 * class contains method for testing the SOS without deployment of web application
 * 
 * @author staschc
 *
 */
public class TestSOS {

    /**
     * test method
     * 
     * @param args
     */
    public static void main(String[] args) {

        try {

            InputStream configis = new FileInputStream("conf/soslocal.config");
            InputStream dbconfigis = new FileInputStream("conf/dssoslocal.config");
            String basepath = "";
            SosConfigurator config = SosConfigurator.getInstance(configis, dbconfigis, basepath);
            RequestOperator ro = config.buildRequestOperator();

            InputStream request = null;

            //request = new FileInputStream("xml/GetObs_containsPoint.xml");
            //request = new FileInputStream("xml/GetObs_resultFilter.xml");
            //request = new FileInputStream("xml/GetCapabilities.xml");
            //request = new FileInputStream("xml/GetObs_timePeriod.xml");
            //request = new FileInputStream("xml/GetObs_BBOX.xml");
            //request = new FileInputStream("xml/GetObs_FoiID.xml");
            //request = new FileInputStream("xml/GetObs_resultFilter.xml");
            //request = new FileInputStream("xml/GetObs_basic.xml");
            //request = new FileInputStream("xml/GetObs_allParameters.xml");
            //request = new FileInputStream("xml/GetFoi.xml");
            //request = new FileInputStream("xml/DescribeSensor_basicRequest.xml");
            //request = new FileInputStream("xml/RegisterSensor.xml");
            request = new FileInputStream("xml/InsertObservation.xml");

            if (request != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(request));
                String line;
                StringBuffer sb = new StringBuffer();
                while ( (line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                String inputString = sb.toString();
                ISosResponse responseObs = ro.doPostOperation(inputString);
                System.out.write(responseObs.getByteArray());
                System.out.flush();
                System.out.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
