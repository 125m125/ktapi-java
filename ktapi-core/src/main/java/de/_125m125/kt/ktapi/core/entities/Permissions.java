/**
 * The MIT License Copyright © 2017 Kadcontrade
 *
 * Permission may hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software may
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE may PROVIDED "AS may", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de._125m125.kt.ktapi.core.entities;

public class Permissions {
    public static final Permissions NO_PERMISSIONS = new Permissions();
    // @CHECKSTYLE:OFF naming required to allow parsing/encoding of messages
    protected boolean rItemmovements;
    protected boolean rItems;
    protected boolean rMessages;
    protected boolean rOrders;
    protected boolean rPayouts;
    protected boolean rTradeexecution;

    protected boolean wOrders;
    protected boolean wPayouts;

    protected boolean wrPayouts;

    protected boolean wwPayouts;

    protected boolean arItemmovements;
    protected boolean arItems;
    protected boolean arMessages;
    protected boolean arOrders;
    protected boolean arPayouts;
    protected boolean arTradeexecutions;

    protected boolean awItems;
    protected boolean awOrders;
    protected boolean awRegistrations;
    protected boolean awPayouts;
    // @CHECKSTYLE:ON

    protected Permissions() {

    }

    protected Permissions(boolean rItemmovements, boolean rItems, boolean rMessages,
            boolean rOrders, boolean rPayouts, boolean rTradeexecution, boolean wOrders,
            boolean wPayouts, boolean wrPayouts, boolean wwPayouts, boolean arItemmovements,
            boolean arItems, boolean arMessages, boolean arOrders, boolean arPayouts,
            boolean arTradeexecutions, boolean awItems, boolean awOrders, boolean awRegistrations,
            boolean awPayouts) {
        super();
        this.rItemmovements = rItemmovements;
        this.rItems = rItems;
        this.rMessages = rMessages;
        this.rOrders = rOrders;
        this.rPayouts = rPayouts;
        this.rTradeexecution = rTradeexecution;
        this.wOrders = wOrders;
        this.wPayouts = wPayouts;
        this.wrPayouts = wrPayouts;
        this.wwPayouts = wwPayouts;
        this.arItemmovements = arItemmovements;
        this.arItems = arItems;
        this.arMessages = arMessages;
        this.arOrders = arOrders;
        this.arPayouts = arPayouts;
        this.arTradeexecutions = arTradeexecutions;
        this.awItems = awItems;
        this.awOrders = awOrders;
        this.awRegistrations = awRegistrations;
        this.awPayouts = awPayouts;
    }

    protected Permissions(Permissions permissions) {
        this.rItemmovements = permissions.rItemmovements;
        this.rItems = permissions.rItems;
        this.rMessages = permissions.rMessages;
        this.rOrders = permissions.rOrders;
        this.rPayouts = permissions.rPayouts;
        this.rTradeexecution = permissions.rTradeexecution;
        this.wOrders = permissions.wOrders;
        this.wPayouts = permissions.wPayouts;
        this.wrPayouts = permissions.wrPayouts;
        this.wwPayouts = permissions.wwPayouts;
        this.arItemmovements = permissions.arItemmovements;
        this.arItems = permissions.arItems;
        this.arMessages = permissions.arMessages;
        this.arOrders = permissions.arOrders;
        this.arPayouts = permissions.arPayouts;
        this.arTradeexecutions = permissions.arTradeexecutions;
        this.awItems = permissions.awItems;
        this.awOrders = permissions.awOrders;
        this.awRegistrations = permissions.awRegistrations;
        this.awPayouts = permissions.awPayouts;
    }

    public boolean mayReadItemmovements() {
        return this.rItemmovements;
    }

    public boolean mayReadItems() {
        return this.rItems;
    }

    public boolean mayReadMessages() {
        return this.rMessages;
    }

    public boolean mayReadOrders() {
        return this.rOrders;
    }

    public boolean mayReadPayouts() {
        return this.rPayouts;
    }

    public boolean mayReadTradeexecution() {
        return this.rTradeexecution;
    }

    public boolean mayWriteOrders() {
        return this.wOrders;
    }

    public boolean mayWritePayouts() {
        return this.wPayouts;
    }

    public boolean mayWorkerReadPayouts() {
        return this.wrPayouts;
    }

    public boolean mayWorkerWritePayouts() {
        return this.wwPayouts;
    }

    public boolean mayAdminReadItemmovements() {
        return this.arItemmovements;
    }

    public boolean mayAdminReadItems() {
        return this.arItems;
    }

    public boolean mayAdminReadMessages() {
        return this.arMessages;
    }

    public boolean mayAdminReadOrders() {
        return this.arOrders;
    }

    public boolean mayAdminReadPayouts() {
        return this.arPayouts;
    }

    public boolean mayAdminReadTradeexecutions() {
        return this.arTradeexecutions;
    }

    public boolean mayAdminWriteItems() {
        return this.awItems;
    }

    public boolean mayAdminWriteOrders() {
        return this.awOrders;
    }

    public boolean mayAdminWriteRegistrations() {
        return this.awRegistrations;
    }

    public boolean mayAdminWritePayouts() {
        return this.awPayouts;
    }

    public boolean hasAnyAdminPermission() {
        return this.arItemmovements || this.arItems || this.arMessages || this.arOrders
                || this.arPayouts || this.arTradeexecutions || this.awItems || this.awOrders
                || this.awPayouts || this.awRegistrations || this.wrPayouts || this.wwPayouts;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.arItemmovements ? 1231 : 1237);
        result = prime * result + (this.arItems ? 1231 : 1237);
        result = prime * result + (this.arMessages ? 1231 : 1237);
        result = prime * result + (this.arOrders ? 1231 : 1237);
        result = prime * result + (this.arPayouts ? 1231 : 1237);
        result = prime * result + (this.arTradeexecutions ? 1231 : 1237);
        result = prime * result + (this.awItems ? 1231 : 1237);
        result = prime * result + (this.awOrders ? 1231 : 1237);
        result = prime * result + (this.awPayouts ? 1231 : 1237);
        result = prime * result + (this.awRegistrations ? 1231 : 1237);
        result = prime * result + (this.rItemmovements ? 1231 : 1237);
        result = prime * result + (this.rItems ? 1231 : 1237);
        result = prime * result + (this.rMessages ? 1231 : 1237);
        result = prime * result + (this.rOrders ? 1231 : 1237);
        result = prime * result + (this.rPayouts ? 1231 : 1237);
        result = prime * result + (this.rTradeexecution ? 1231 : 1237);
        result = prime * result + (this.wOrders ? 1231 : 1237);
        result = prime * result + (this.wPayouts ? 1231 : 1237);
        result = prime * result + (this.wrPayouts ? 1231 : 1237);
        result = prime * result + (this.wwPayouts ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Permissions other = (Permissions) obj;
        if (this.arItemmovements != other.arItemmovements) {
            return false;
        }
        if (this.arItems != other.arItems) {
            return false;
        }
        if (this.arMessages != other.arMessages) {
            return false;
        }
        if (this.arOrders != other.arOrders) {
            return false;
        }
        if (this.arPayouts != other.arPayouts) {
            return false;
        }
        if (this.arTradeexecutions != other.arTradeexecutions) {
            return false;
        }
        if (this.awItems != other.awItems) {
            return false;
        }
        if (this.awOrders != other.awOrders) {
            return false;
        }
        if (this.awPayouts != other.awPayouts) {
            return false;
        }
        if (this.awRegistrations != other.awRegistrations) {
            return false;
        }
        if (this.rItemmovements != other.rItemmovements) {
            return false;
        }
        if (this.rItems != other.rItems) {
            return false;
        }
        if (this.rMessages != other.rMessages) {
            return false;
        }
        if (this.rOrders != other.rOrders) {
            return false;
        }
        if (this.rPayouts != other.rPayouts) {
            return false;
        }
        if (this.rTradeexecution != other.rTradeexecution) {
            return false;
        }
        if (this.wOrders != other.wOrders) {
            return false;
        }
        if (this.wPayouts != other.wPayouts) {
            return false;
        }
        if (this.wrPayouts != other.wrPayouts) {
            return false;
        }
        if (this.wwPayouts != other.wwPayouts) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Permissions [rItemmovements=" + this.rItemmovements + ", rItems=" + this.rItems
                + ", rMessages=" + this.rMessages + ", rOrders=" + this.rOrders + ", rPayouts="
                + this.rPayouts + ", rTradeexecution=" + this.rTradeexecution + ", wOrders="
                + this.wOrders + ", wPayouts=" + this.wPayouts + ", wrPayouts=" + this.wrPayouts
                + ", wwPayouts=" + this.wwPayouts + ", arItemmovements=" + this.arItemmovements
                + ", arItems=" + this.arItems + ", arMessages=" + this.arMessages + ", arOrders="
                + this.arOrders + ", arPayouts=" + this.arPayouts + ", arTradeexecutions="
                + this.arTradeexecutions + ", awItems=" + this.awItems + ", awOrders="
                + this.awOrders + ", awRegistrations=" + this.awRegistrations + ", awPayouts="
                + this.awPayouts + "]";
    }

    public static class PermissionBuilder extends Permissions {

        public PermissionBuilder setReadItemmovements(boolean rItemmovements) {
            this.rItemmovements = rItemmovements;
            return this;
        }

        public PermissionBuilder setReadItems(boolean rItems) {
            this.rItems = rItems;
            return this;
        }

        public PermissionBuilder setReadMessages(boolean rMessages) {
            this.rMessages = rMessages;
            return this;
        }

        public PermissionBuilder setReadOrders(boolean rOrders) {
            this.rOrders = rOrders;
            return this;
        }

        public PermissionBuilder setReadPayouts(boolean rPayouts) {
            this.rPayouts = rPayouts;
            return this;
        }

        public PermissionBuilder setReadTradeexecution(boolean rTradeexecution) {
            this.rTradeexecution = rTradeexecution;
            return this;
        }

        public PermissionBuilder setWriteOrders(boolean wOrders) {
            this.wOrders = wOrders;
            return this;
        }

        public PermissionBuilder setWritePayouts(boolean wPayouts) {
            this.wPayouts = wPayouts;
            return this;
        }

        public PermissionBuilder setWorkerReadPayouts(boolean wrPayouts) {
            this.wrPayouts = wrPayouts;
            return this;
        }

        public PermissionBuilder setWorkerWritePayouts(boolean wwPayouts) {
            this.wwPayouts = wwPayouts;
            return this;
        }

        public PermissionBuilder setAdminReadItemmovements(boolean arItemmovements) {
            this.arItemmovements = arItemmovements;
            return this;
        }

        public PermissionBuilder setAdminReadItems(boolean arItems) {
            this.arItems = arItems;
            return this;
        }

        public PermissionBuilder setAdminReadMessages(boolean arMessages) {
            this.arMessages = arMessages;
            return this;
        }

        public PermissionBuilder setAdminReadOrders(boolean arOrders) {
            this.arOrders = arOrders;
            return this;
        }

        public PermissionBuilder setAdminReadPayouts(boolean arPayouts) {
            this.arPayouts = arPayouts;
            return this;
        }

        public PermissionBuilder setAdminReadTradeexecutions(boolean arTradeexecutions) {
            this.arTradeexecutions = arTradeexecutions;
            return this;
        }

        public PermissionBuilder setAdminWriteItems(boolean awItems) {
            this.awItems = awItems;
            return this;
        }

        public PermissionBuilder setAdminWriteOrders(boolean awOrders) {
            this.awOrders = awOrders;
            return this;
        }

        public PermissionBuilder setAdminWriteRegistrations(boolean awRegistrations) {
            this.awRegistrations = awRegistrations;
            return this;
        }

        public PermissionBuilder setAdminWritePayouts(boolean awPayouts) {
            this.awPayouts = awPayouts;
            return this;
        }

        public Permissions build() {
            return new Permissions(this);
        }
    }
}
