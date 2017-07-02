package com.minelife.permission;

import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

public class Group {

    String name;

    protected Group(String name) throws Exception {
        for (Group g : getGroups())
            if (g.name.equalsIgnoreCase(name)) {
                this.name = g.name;
                break;
            }

        if (this.name == null) throw new Exception("Group " + name + " does not exist.");
    }

    public String getName() {
        return name;
    }

    Set<String> getPermissions() {
        JSONObject object = getJSONObject();
        Set<String> permissions = new TreeSet<>();

        if (object != null && object.get("permissions") != null) {
            JSONArray permissionArray = (JSONArray) object.get("permissions");
            ListIterator permissionIterator = permissionArray.listIterator();

            while (permissionIterator.hasNext()) {
                String perm = String.valueOf(permissionIterator.next());
                permissions.add(perm);
            }
        }

        return permissions;
    }

    Set<String> getInheritance() {
        JSONObject object = getJSONObject();

        Set<String> inheritance = new TreeSet<>();

        if (object != null && object.get("inheritance") != null) {
            ListIterator inheritanceIterator = ((JSONArray) object.get("inheritance")).listIterator();
            while (inheritanceIterator.hasNext()) inheritance.add(String.valueOf(inheritanceIterator.next()));
        }

        return inheritance;
    }

    boolean isDefaultGroup() {
        JSONObject object = getJSONObject();

        if (object != null && object.get("default") != null)
            return Boolean.valueOf(String.valueOf(object.get("default")));

        return false;
    }

    String getPrefix() {
        JSONObject object = getJSONObject();

        if (object != null && object.get("prefix") != null)
            return String.valueOf(object.get("prefix")).replaceAll("&", String.valueOf('\u00a7'));

        return null;
    }

    String getSuffix() {
        JSONObject object = getJSONObject();

        if (object != null && object.get("suffix") != null)
            return String.valueOf(object.get("suffix")).replaceAll("&", String.valueOf('\u00a7'));

        return null;
    }

    void addPermission(String permission) {
        JSONObject object = getJSONObject();

        if (object != null && object.get("permissions") != null)
            ((JSONArray) object.get("permissions")).add(permission.toLowerCase());

        ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
    }

    void removePermission(String permission) {
        JSONObject object = getJSONObject();

        if (object != null && object.get("permissions") != null)
            ((JSONArray) object.get("permissions")).remove(permission.toLowerCase());

        ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
    }

    Set<String> getAllInheritedPermissions() throws Exception {
        Set<String> permissions = new TreeSet<>();
        JSONObject object = getJSONObject();

        if (object == null) return permissions;

        // add all of *this* groups permissions
        permissions.addAll(getPermissions());

        /*
         * go through all the inherited groups permissions and add their permissions
         * and their inherited permissions
         */
        for (String gName : getInheritance())
            permissions.addAll(new Group(gName).getAllInheritedPermissions());

        return permissions;
    }

    JSONObject getJSONObject() {
        JSONArray jsonArray = (JSONArray) ServerProxy.JSON_OBJECT.get("groups");

        if (jsonArray == null) return null;

        ListIterator listIterator = jsonArray.listIterator();

        while (listIterator.hasNext()) {
            JSONObject object = (JSONObject) listIterator.next();

            if (object.get("name") != null && String.valueOf(object.get("name")).equalsIgnoreCase(name))
                return object;
        }

        return null;
    }

    public static List<Group> getGroups() {
        JSONArray jsonArray = (JSONArray) ServerProxy.JSON_OBJECT.get("groups");

        List<Group> groups = Lists.newArrayList();

        if (jsonArray == null) return groups;

        ListIterator listIterator = jsonArray.listIterator();

        while (listIterator.hasNext()) {
            JSONObject object = (JSONObject) listIterator.next();
            try {
                groups.add(new Group(String.valueOf(object.get("name"))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return groups;
    }
}
