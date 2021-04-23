import React from 'react';
import {Card} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import temp from "../assets/temp.svg";
import {Link} from "react-router-dom";

const NoticeboardItem = (props) => {
    return (
        <Link to={`/notice/${props.id}`} className="col-12 col-md-6 col-lg-4 my-2">
            <Card className="col-11 mx-auto list-group-item-action" style={{cursor: 'pointer'}}>
                <div className="mx-auto">
                    <h6><FontAwesomeIcon icon="map-pin"/></h6>
                </div>
                <Card.Img variant="top" src={temp}/>
                <Card.Body>
                    <Card.Title>{props.title}</Card.Title>
                    <Card.Text as="span">
                        <div><FontAwesomeIcon icon="wallet" className="mx-1"/>{props.price}</div>
                        <div><FontAwesomeIcon icon="map-marker-alt" className="mx-1"/>{props.location}</div>
                    </Card.Text>
                </Card.Body>
                <Card.Footer>
                    <small className="text-muted">Opublikowano: 3 minuty temu</small>
                </Card.Footer>
            </Card>
        </Link>
    );
}

export default NoticeboardItem;