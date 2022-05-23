import React from 'react';
import { Navbar, Nav } from 'react-bootstrap'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faBars, faHome, faUser} from '@fortawesome/free-solid-svg-icons'
import {Link, useNavigate} from 'react-router-dom';
import Utils from "../utils/Utils";
import BackendService from "../services/BackendService";
import {connect} from "react-redux"
import {userActions} from "../utils/Rdx";

const NavigationBar = (props) => {
    const navigate = useNavigate();

    function logout() {
        BackendService.logout().then(() => {
            Utils.removeUser();
            props.dispatch(userActions.logout())
            navigate("/login")
        });
    }

    return (
        <Navbar bg="light" expand="lg">
            <button type="button"
                    style={{marginLeft: "10px"}}
                    className="btn btn-outline-secondary mr-2"
                    onClick={props.toggleSideBar}>
                <FontAwesomeIcon icon={faBars} />
            </button>
            <Navbar.Brand style={{marginLeft: "10px"}}>Gaplikov RPO</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="me-auto">
                    <Nav.Link as={Link} to="/home">Home</Nav.Link>
                </Nav>
                <Navbar.Text>{props.user && props.user.login}</Navbar.Text>
                { props.user && <Nav.Link onClick={logout}><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Выход</Nav.Link>}
                { !props.user && <Nav.Link as={Link} to="/login"><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Вход</Nav.Link>}
            </Navbar.Collapse>
        </Navbar>
    );
}

const mapStateToProps = state => {
    const { user } = state.authentication;
    return { user };
}
export default connect(mapStateToProps)(NavigationBar);