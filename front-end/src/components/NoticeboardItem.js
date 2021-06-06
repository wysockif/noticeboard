import React from 'react';
import {Card, Image} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import defaultNoticeImage from "../assets/default-notice-image.jpg";
import {Link} from "react-router-dom";
import moment from "moment";
import 'moment/locale/pl'

const NoticeboardItem = (props) => {
    const imageSource = props.image ? '/images/notice/' + props.image : defaultNoticeImage;
    const momentDate = moment(new Date(props.createdAt));
    momentDate.locale('pl');

    return (
        <div className="col-12 col-md-6 col-lg-4 mt-4">
            <Link to={`/notice/${props.id}`} className="text-decoration-none">
                <Card className="col-12 mx-auto list-group-item-action" style={{cursor: 'pointer'}}>
                    <div className="mx-auto">
                        <h6><FontAwesomeIcon icon="map-pin"/></h6>
                    </div>
                    <Image src={imageSource} onError={event => event.target.src = defaultNoticeImage}
                           className="shadow-sm objectFit" style={{borderTop: "1px solid whitesmoke"}}/>
                    <Card.Body>
                        <Card.Title className="text-nowrap text-truncate">{props.title}</Card.Title>
                        <Card.Text as="div" className="col">
                            <div>
                                <FontAwesomeIcon icon="wallet" className="ms-1 me-1 pe-1"/>
                                <span>{props.price} zł</span>
                            </div>
                            <div className="text-nowrap text-truncate">
                                <FontAwesomeIcon icon="map-marker-alt" className="ms-1 me-2"/>
                                {props.location}
                            </div>
                        </Card.Text>
                    </Card.Body>
                    <Card.Footer>
                        <small className="text-muted">Opublikowano: {momentDate.startOf('second').fromNow()}</small>
                    </Card.Footer>
                </Card>
            </Link>
        </div>
    );
}

export default NoticeboardItem;