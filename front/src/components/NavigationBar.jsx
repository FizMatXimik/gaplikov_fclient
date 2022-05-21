import React from 'react';
import { Navbar, Nav } from 'react-bootstrap'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faHome, faUser} from '@fortawesome/free-solid-svg-icons'
import { useNavigate, Link } from 'react-router-dom';
import Utils from "../utils/Utils";
import BackendService from "../services/BackendService";

const NavigationBar = ({props}) => {
    const navigate = useNavigate()
    let uname = Utils.getUserName();

    function logout() {
        BackendService.logout().then(() => {
            Utils.removeUser();
            navigate("/home")
        });
    }

    return (
        <Navbar bg="light" expand="lg">
            <Navbar.Brand><FontAwesomeIcon icon={faHome} />{' '}Gaplikov RPO</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="me-auto">
                    <Nav.Link as={Link} to="/home">Home</Nav.Link>
                </Nav>
                <Navbar.Text>{uname}</Navbar.Text>
                { uname && <Nav.Link onClick={logout}><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Выход</Nav.Link>}
                { !uname && <Nav.Link as={Link} to="/login"><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Вход</Nav.Link>}
            </Navbar.Collapse>
        </Navbar>
    );
}

export default NavigationBar;