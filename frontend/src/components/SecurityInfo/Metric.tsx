
interface MetricProps {
    title: string,
    value: string,
    styles?: string
}

function Metric({ title, value, styles }: MetricProps) {
    return (
        <div className="info-section">
            <h4 className="side-text">{title}</h4>
            <p className={value !== "-" ? styles : ""}>{value}</p>
        </div>
    )
}

export default Metric;