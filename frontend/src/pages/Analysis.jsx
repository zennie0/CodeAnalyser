import { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, Braces, Upload, Play, AlertTriangle, CheckCircle2, Gauge } from 'lucide-react';

const sample = `public class Example {
  public void compare(int[] numbers) {
    for (int i = 0; i < numbers.length; i++) {
      for (int j = 0; j < numbers.length; j++) {
        System.out.println(numbers[i] + numbers[j]);
      }
    }
  }
}`;

/** Lets a user submit source and presents API findings in an easy-to-scan dashboard. */
export default function Analysis() {
  const [code, setCode] = useState(sample), [file, setFile] = useState(null), [result, setResult] = useState(null), [error, setError] = useState(''), [loading, setLoading] = useState(false);
  const fileInput = useRef(), lineNumbers = useRef();
  /** Validates a Java file and loads its contents into the editor for review before analysis. */
  const chooseFile = event => {
    const picked = event.target.files?.[0];
    if (!picked) return;
    if (!picked.name.toLowerCase().endsWith('.java')) { setError('Only one .java file can be uploaded.'); return; }
    const reader = new FileReader();
    reader.onload = loadEvent => { setCode(String(loadEvent.target?.result ?? '')); setFile(picked); setError(''); };
    reader.onerror = () => { setFile(null); setError('The selected file could not be read. Please choose a valid UTF-8 .java file.'); };
    reader.readAsText(picked);
  };
  /** Sends either the uploaded file or typed code to its dedicated REST endpoint. */
  const analyze = async () => { setLoading(true); setError(''); setResult(null); try { let response; if (file) { const form = new FormData(); form.append('file', file); response = await fetch('http://localhost:8080/api/analyze/file', { method: 'POST', body: form }); } else { response = await fetch('http://localhost:8080/api/analyze', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({code}) }); } const body = await response.json(); if (!response.ok) throw new Error(body.error || 'Analysis failed.'); setResult(body); } catch (e) { setError(e.message); } finally { setLoading(false); } };
  return <main className="shell analysis"><nav><Link to="/" className="back"><ArrowLeft size={17}/> Back</Link><span className="brand"><Braces/> Code<span>Lens</span></span></nav><header><div><div className="eyebrow">ANALYSIS CONSOLE</div><h1>Review your Java code.</h1><p>One source file at a time. No source is stored.</p></div></header><section className="workspace"><div className="editor-card"><div className="editor-top"><span>JAVA SOURCE</span><button onClick={() => fileInput.current.click()} className="upload"><Upload size={15}/>{file ? file.name : 'Upload .java'}</button><input ref={fileInput} type="file" accept=".java" onChange={chooseFile} hidden/></div><div className="code-editor"><pre ref={lineNumbers} className="line-numbers" aria-hidden="true">{code.split('\n').map((_, index) => <span key={index}>{index + 1}</span>)}</pre><textarea value={code} onScroll={event => { if (lineNumbers.current) lineNumbers.current.scrollTop = event.currentTarget.scrollTop; }} onChange={e=>{setCode(e.target.value); setFile(null);}} spellCheck="false" aria-label="Java source code" placeholder="Paste Java code here..."/></div><div className="editor-foot"><span>{file ? 'File loaded — edit to switch to pasted-source analysis' : `${code.split('\n').length} lines`}</span><button className="primary analyze-btn" disabled={loading || (!file && !code.trim())} onClick={analyze}>{loading ? 'Analyzing…' : <>Analyze code <Play size={16}/></>}</button></div></div><Results result={result} error={error}/></section></main>;
}

/** Renders empty, loading-independent analysis output in a consistent layout. */
function Results({result, error}) { if (error) return <aside className="results error"><AlertTriangle/><h2>Couldn’t analyze</h2><p>{error}</p></aside>; if (!result) return <aside className="results empty"><Gauge/><h2>Ready when you are</h2><p>Your complexity report, score and line-level recommendations will appear here.</p></aside>; if (result.syntaxErrors?.length) return <aside className="results error syntax-results"><AlertTriangle/><h2>Java syntax needs attention</h2><p>Fix these issues before running the full quality analysis.</p><div className="finding-list">{result.syntaxErrors.map((issue,i)=><article className="finding error" key={i}><div><b>{issue.line ? `LINE ${issue.line}${issue.column ? `, COLUMN ${issue.column}` : ''}` : 'LOCATION UNKNOWN'}</b><h4>Syntax error</h4><p>{issue.message}</p><small>↳ {issue.suggestion}</small></div></article>)}</div></aside>; return <aside className="results"><div className="score"><div><span>QUALITY SCORE</span><strong>{result.score}<small>/10</small></strong><p>{result.scoreLabel}</p></div><div className="ring" style={{'--score': `${result.score * 10}%`}}>{result.score}</div></div><div className="metrics"><Metric label="Time" value={result.timeComplexity}/><Metric label="Space" value={result.spaceComplexity}/><Metric label="Nested loops" value={result.nestedLoopCount}/></div><h3>Findings <span>{result.findings.length}</span></h3><div className="finding-list">{result.findings.length === 0 ? <div className="good"><CheckCircle2/> Nothing concerning found.</div> : result.findings.map((f,i)=><article className={`finding ${f.severity}`} key={i}><div><b>LINE {f.line}</b><h4>{f.title}</h4><p>{f.detail}</p><small>↳ {f.suggestion}</small></div></article>)}</div></aside>; }
/** Displays one headline metric without repeating styling markup. */
function Metric({label,value}) { return <div><span>{label}</span><b>{value}</b></div>; }
