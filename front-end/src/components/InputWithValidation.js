import React from 'react';
import { InputGroup, FormControl } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';


const InputWithValidation = props => {
    const type = props.type ? props.type : 'text';
    return (
        <InputGroup className="mb-3">
            <InputGroup.Prepend style={{ minWidth: '200px' }}>
                <InputGroup.Text id="basic-addon1">
                    <FontAwesomeIcon icon={props.icon} className="my-2 me-2 " /> {props.placeholder}:
                </InputGroup.Text>
            </InputGroup.Prepend>
            <FormControl
                placeholder={props.placeholder}
                aria-label={props.placeholder}
                aria-describedby="basic-addon1"
                value={props.value}
                onChange={props.onChange}
                type={type}
            />
        </InputGroup>
    );
}

export default InputWithValidation;