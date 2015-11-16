package controllers;

import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import models.User;

import java.util.List;

import static play.data.Form.form;

/**
 * This is the User Controller. This is where we implement all of the Action
 * methods related to operations on my User.
 */
public class UserAuth extends Controller {

    // Route: GET /user/new
    //  Displays the form for creating a new user account.
    public Result form() {
        return ok(views.html.user.form.render());
    }

    // Route: POST /user
    //  Creates a User account from user request input.
    public Result create() {
        DynamicForm userForm = Form.form().bindFromRequest();
        String username = userForm.data().get("userName");
        String password = userForm.data().get("password");

        User user = User.createNewUser(username, password);

        if(user == null) {
            flash("error", "Invalid user");
            return redirect(routes.Application.index());
        }

        user.save();

        flash("success", "Welcome new user " + user.getUserName());
        session("user_id", user.getID().toString());
        return redirect(routes.UserAuth.show(user.getID()));
    }

    // Route: DELETE /user/:id
    //  Delete a User account from data base.
    public Result delete(String id) {
        return ok();
    }

    // Route: PUT   /user/:id
    //  Update a User account by using it's registered form.
    public Result update(String id) {
        return ok();
    }

    // Route: GET /users/:id
    //  Shows the User profile 'id'
    //  There's an issue here... why is 'new' in user/new NOT taken as an 'id' but 'login' in user/login IS taken as 'id'
    public Result show(String id) {
        return ok("This is the user/:id page for user profile of id: " + String.valueOf(id) + "\n");
    }


}
