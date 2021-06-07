package neprowaet.jpcw.data;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationData extends Data {

    public long userid;

    public long localsid;

    public int roleSlot;

    public long selectedRoleid;


    public List<RoleData> roles = new ArrayList<>();

    public void addRole(long roleid) {
        roles.add(new RoleData(roleid));
    }

    public class RoleData {
        public RoleData(long roleid) {
            this.roleid = roleid;
        }

        public long roleid;
    }
}
