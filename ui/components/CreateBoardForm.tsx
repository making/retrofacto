import * as React from 'react';
import {useState} from 'react';
import styled from 'styled-components';
import {StyledInput} from "./StyledInput.tsx";
import {GreenButton} from "./GreenButton.tsx";
import {Link} from "react-router-dom";
import {StyledForm} from "./StyledForm.tsx";


const MessageBox = styled.div`
  padding: 20px;
  margin: 20px 0;
  color: #155724;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 0.25rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  gap: 10px;
`;

const MessageText = styled.p`
  margin: 0;
  flex-grow: 1;
`;

interface BoardFormProps {

}

const BoardForm: React.FC<BoardFormProps> = () => {
    const [formData, setFormData] = useState({
        name: '',
        slug: '',
        passphrase: '',
    });

    const [created, setCreated] = useState<boolean>(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData({...formData, [name]: value});
    };

    const handleNameBlur = () => {
        const newSlug = formData.name
            .toLowerCase()
            .replace(/\s+/g, '-')
            .replace(/[^a-z0-9-]/g, '');
        setFormData({...formData, slug: newSlug});
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        fetch('http://localhost:8080/boards', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        }).then(async response => {
            if (response.status === 201) {
                setCreated(true);
            } else {
                const json = await response.json();
                if (json.message) {
                    alert(`${response.status}: ${json.message}`);
                } else {
                    alert(`${response.status}: ${JSON.stringify(json)}`);
                }
            }
        });
    };

    return (created ?
            <MessageBox>
                <MessageText>✅ New board ("{formData.name}") has been created!</MessageText>
                <MessageText>Go to <Link to={`/retros/${formData.slug}`} reloadDocument={true}>the
                    board</Link> !</MessageText>
            </MessageBox>
            :
            <div>
                <h2>Create a new retro board</h2>
                <StyledForm onSubmit={handleSubmit}>
                    <StyledInput
                        name="name"
                        type="text"
                        placeholder="Name"
                        value={formData.name}
                        onChange={handleChange}
                        onBlur={handleNameBlur}
                        required={true}
                    />
                    <StyledInput
                        name="slug"
                        type="text"
                        placeholder="Slug"
                        value={formData.slug}
                        onChange={handleChange}
                        required={true}
                    />
                    <StyledInput
                        name="passphrase"
                        type="password"
                        placeholder="Passphrase (optional)"
                        value={formData.passphrase}
                        onChange={handleChange}
                    />
                    <GreenButton type="submit">Create Board</GreenButton>
                </StyledForm>
            </div>
    )
        ;
};

export default BoardForm;
