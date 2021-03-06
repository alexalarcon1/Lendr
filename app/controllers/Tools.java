package controllers;

import models.User;
import models.Tool;
import models.Comment;
import models.ToolCategory;
import play.mvc.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import javax.persistence.*;
import play.db.ebean.Model;


import java.util.List;


import static play.data.Form.form;

/**
 * This is a Tool controller that implements RESTful routes. You can test
 * these routes using the CURL utility.
 */
public class Tools extends Controller {

    // Route: GET /tool
    @Security.Authenticated(UserAuth.class)
    public Result index() {
        Long ownerId = Long.parseLong(session().get("user_id"));
        User owner = User.find.byId(ownerId);
        List<Tool> tools = owner.toolList;
        List<ToolCategory> categories = ToolCategory.find.all();

        return ok(views.html.tool.index.render(tools, categories));
    }

    @Security.Authenticated(UserAuth.class)
    public Result borrowIndex() {
        Long ownerId = Long.parseLong(session().get("user_id"));
        User owner = User.find.byId(ownerId);
        List<Tool> tools = owner.borrowToolList;
        List<ToolCategory> categories = ToolCategory.find.all();

        return ok(views.html.tool.borrowindex.render(tools, categories));
    } 

    // Route: GET /tool/:id
    @Security.Authenticated(UserAuth.class)
    public Result show(Long id) {
        Long ownerId = Long.parseLong(session().get("user_id"));
        User owner = User.find.byId(ownerId);
        Tool tool = Tool.find.byId(id);
        List<Comment> comments = tool.commentList;

        return ok(views.html.tool.item.render(tool, owner, comments));
    }

    // Route: DELETE /tool/:id
    @Security.Authenticated(UserAuth.class)
    public Result delete(Long id) {
        Tool tool = Tool.find.byId(id);
        tool.delete();

        Long ownerId = Long.parseLong(session().get("user_id"));
        User owner = User.find.byId(ownerId);
        List<Tool> tools = owner.toolList;
        List<ToolCategory> categories = ToolCategory.find.all();

        return ok(views.html.tool.index.render(tools, categories));
    }

    // Route: GET /tool/:id/edit
    public Result edit(Long id) {
        return ok();
    }

    // Route: GET /borrow/:id
    @Security.Authenticated(UserAuth.class)
    public Result lend(Long id) {
        Long ownerId = Long.parseLong(session().get("user_id"));
        User owner = User.find.byId(ownerId);
        Tool tool = Tool.find.byId(id);
        tool.available = true;
        tool.save();
        List<Comment> comments = tool.commentList;

       return ok(views.html.tool.item.render(tool,owner,comments));
    }

    // Route: GET /borrow/:id
    @Security.Authenticated(UserAuth.class)
    public Result borrow(Long id) {
        Long userId = Long.parseLong(session().get("user_id"));
        User user = User.find.byId(userId);
        Tool tool = Tool.find.byId(id);
        tool.borrower = user;
        tool.available = false;
        tool.save();
        List<Comment> comments = tool.commentList;

       return ok(views.html.tool.item.render(tool,user,comments));
    }

    // Route: GET /return/:id
    @Security.Authenticated(UserAuth.class)
    public Result returnTool(Long id) {
        Tool tool = Tool.find.byId(id);
        tool.borrower = null;
        tool.available = true;
        tool.save();

        Long ownerId = Long.parseLong(session().get("user_id"));
        User owner = User.find.byId(ownerId);
        List<Tool> tools = owner.borrowToolList;
        List<ToolCategory> categories = ToolCategory.find.all();

       return ok(views.html.tool.borrowindex.render(tools, categories));
    }

    // Route: GET /tool/new
    @Security.Authenticated(UserAuth.class)
    public Result createForm() {
        List<ToolCategory> categories = ToolCategory.find.all();

        return ok(views.html.tool.createform.render(categories));
    }

    // Route: POST /tool
    @Security.Authenticated(UserAuth.class)
    public Result create() {
        DynamicForm toolForm = Form.form().bindFromRequest();
        String name = toolForm.data().get("name");
        String description = toolForm.data().get("description");
        String categoryId = toolForm.data().get("category_id");
        String image = toolForm.data().get("image");

        Long ownerId = Long.parseLong(session().get("user_id"));
        User owner = User.find.byId(ownerId);

        ToolCategory tool_category = ToolCategory.find.byId(Long.parseLong(categoryId));

        Tool tool = Tool.createNewTool (name,description,owner,tool_category, image);

        if(tool == null ){
            flash("error", "Invalid tool, please enter all required informations");
            return redirect(routes.Tools.createForm());
        }

        tool.save();

        flash("success", "Added new tool"+tool.name);
        return redirect(routes.UserActivity.show());
    }

    @Security.Authenticated(UserAuth.class)
    public Result categoryFilter(Long category_id){
        List<ToolCategory> categories = ToolCategory.find.all();
        Long ownerId = Long.parseLong(session().get("user_id"));
        List<Tool> tools = Tool.searchByUserAndCategory(ownerId, category_id, 1);

        return ok(views.html.tool.index.render(tools, categories));
    }

    @Security.Authenticated(UserAuth.class)
    public Result borrowCategoryFilter(Long category_id){
        List<ToolCategory> categories = ToolCategory.find.all();
        Long ownerId = Long.parseLong(session().get("user_id"));
        List<Tool> tools = Tool.searchByUserAndCategory(ownerId, category_id, 0);

        return ok(views.html.tool.borrowindex.render(tools, categories));
    }

}
