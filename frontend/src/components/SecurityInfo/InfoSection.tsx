
interface InfoSectionProps {
    title: string,
    value: string
}

function InfoSection({ title, value }: InfoSectionProps) {
    return (
        <div className="info-section">
            <h4 className="side-text">{title}</h4>
            <p>{value}</p>
        </div>
    )
}

export default InfoSection;