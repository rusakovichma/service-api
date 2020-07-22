    -- Project 2
    INSERT INTO project (id, name, project_type, creation_date, metadata) VALUES (22, 'personal2', 'PERSONAL', now(), '{"metadata": {"additional_info": ""}}');

    -- Project 2 Member
    INSERT INTO users (id, login, password, email, role, type, full_name, expired, metadata)
    VALUES (22, 'project2_member', '3fde6bb0541387e4ebdadf7c2ff31123', 'project2_member@domain.com', 'USER', 'INTERNAL', 'Test User2', FALSE,
            '{"metadata": {"last_login": 0}}');

    INSERT INTO project_user (user_id, project_id, project_role) VALUES (22, 22, 'MEMBER');

    -- Project 2 Manager
    INSERT INTO users (id, login, password, email, role, type, full_name, expired, metadata)
    VALUES (23, 'project2_manager', '3fde6bb0541387e4ebdadf7c2ff31123', 'project2_manager@domain.com', 'USER', 'INTERNAL', 'Test Manager2', FALSE,
                '{"metadata": {"last_login": 0}}');

    INSERT INTO project_user (user_id, project_id, project_role) VALUES (23, 22, 'PROJECT_MANAGER');

    -- Project 2 Customer
    INSERT INTO users (id, login, password, email, role, type, full_name, expired, metadata)
    VALUES (24, 'project2_customer', '3fde6bb0541387e4ebdadf7c2ff31123', 'project2_customer@customerdomain.com', 'USER', 'INTERNAL', 'Customer Test User', FALSE,
            '{"metadata": {"last_login": 0}}');

    INSERT INTO project_user (user_id, project_id, project_role) VALUES (24, 22, 'CUSTOMER');

    -- Project configurations
    INSERT INTO issue_type_project (project_id, issue_type_id) VALUES (22, 1),(22, 2),(22, 3),(22, 4),(22, 5);

    INSERT INTO project_attribute (attribute_id, value, project_id) VALUES (1, '1 day', 22);
    INSERT INTO project_attribute (attribute_id, value, project_id) VALUES (2, '3 months', 22);
    INSERT INTO project_attribute (attribute_id, value, project_id) VALUES (3, '2 weeks', 22);
    INSERT INTO project_attribute (attribute_id, value, project_id) VALUES (4, '2 weeks', 22);