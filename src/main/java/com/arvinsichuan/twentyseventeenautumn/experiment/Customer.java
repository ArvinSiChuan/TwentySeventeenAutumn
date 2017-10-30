/*
 *     This project is part of project Dawn, A Data Process Solution.
 *     Copyright (C) 2017, Dawn team<https://github.com/Dawn-Team>.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.arvinsichuan.twentyseventeenautumn.experiment;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Project experiment
 * <p>
 * Author: arvinsc@foxmail.com
 * <p>
 * Date: 29-Oct-17
 * <p>
 * Package: com.arvinsichuan.twentyseventeenautumn.experiment
 */
public class Customer  {

    @Id
    public String id;

    public String[] titleName;
    public String[][] contents;

    public Customer() {
    }

    public Customer(String[] titles, String[][] contents) {
        this.titleName = titles;
        this.contents = contents;
    }


    public String[] getTitleName() {
        return titleName;
    }

    public void setTitleName(String[] titleName) {
        this.titleName = titleName;
    }

    public String[][] getContents() {
        return contents;
    }

    public void setContents(String[][] contents) {
        this.contents = contents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String string = id;
        String titles = "";
        for (String str :
                titleName) {
            titles += str + ",";
        }
        String content = "";
        for (String[] str :
                contents) {
            for (String str2 :
                    str) {
                content += str2 + ",";
            }
        }
        return string + titles + "--" + content;
    }
}