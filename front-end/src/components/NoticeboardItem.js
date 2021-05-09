import React from 'react';
import {Card, Image} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import temp from "../assets/temp.svg";
import {Link} from "react-router-dom";
import moment from "moment";
import 'moment/locale/pl'

const NoticeboardItem = (props) => {
    const imageSource = props.image ? 'images/notice/' + props.image : temp;
    const momentDate = moment(new Date(props.createdAt));
    momentDate.locale('pl');

    return (
        <Link to={`/notice/${props.id}`} className="col-12 col-md-6 col-lg-4 my-2 nav-link">
            <Card className="col-11 mx-auto list-group-item-action" style={{cursor: 'pointer'}}>
                <div className="mx-auto">
                    <h6><FontAwesomeIcon icon="map-pin"/></h6>
                </div>
                <Image src={imageSource} onError={event => event.target.src = temp}
                       className="shadow-sm" style={{borderTop: "1px solid whitesmoke"}}/>
                <Card.Body>
                    <Card.Title className="text-nowrap text-truncate">{props.title}</Card.Title>
                    <div className="d-flex align-items-center">
                        <Card.Text as="div" className="col">
                            <div>
                                <FontAwesomeIcon icon="wallet" className="ms-1 me-1 pe-1"/>
                                {props.price} zł
                            </div>
                            <div>
                                <FontAwesomeIcon icon="map-marker-alt" className="ms-1 me-2"/>
                                {props.location}
                            </div>
                        </Card.Text>
                    </div>
                </Card.Body>
                <Card.Footer>
                    <small className="text-muted">Opublikowano: {momentDate.startOf('second').fromNow()}</small>
                </Card.Footer>
            </Card>
        </Link>
    )
        ;
}

export default NoticeboardItem;