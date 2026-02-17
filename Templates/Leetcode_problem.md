**Question :**



**Solution:**

```java

```

<%*
console.log("LeetCode auto-log running...");

const sheetPath = "Writeups/Leetcode/LeetCode Sheet.md";
new Notice("Updating: " + sheetPath);

const today = tp.date.now("DD/MM/YYYY");
const problemName = tp.file.title;

const sheetFile = app.vault.getAbstractFileByPath(sheetPath);
if (!sheetFile) {
  new Notice("❌ Sheet not found!");
  return;
}

let sheet = await app.vault.read(sheetFile);

// =======================
// Parse existing days
// =======================

// Match: # Day 1 - *11/01/2026*
const dayRegex = /# Day (\d+) - \*(\d{2}\/\d{2}\/\d{4})\*/g;

let match;
let maxDay = 0;
let todayDayHeader = null;

while ((match = dayRegex.exec(sheet)) !== null) {
  const dayNum = parseInt(match[1]);
  const dateStr = match[2];

  if (dayNum > maxDay) maxDay = dayNum;
  if (dateStr === today) todayDayHeader = match[0];
}

const linkLine = `[${problemName}](${problemName})`;

// =======================
// If today's day exists → append at end
// =======================
if (todayDayHeader) {
  console.log("Found today's day, appending at end of file...");

  if (!sheet.includes(linkLine)) {
    sheet = sheet.trimEnd() + `\n${linkLine}\n`;
  }
} 
// =======================
// Else → create new day at bottom
// =======================
else {
  const newDayNumber = maxDay + 1;
  const newDayHeader = `# Day ${newDayNumber} - *${today}*\n\n`;

  console.log("Creating new day:", newDayHeader);

  sheet =
    sheet.trimEnd() +
    `\n\n****\n${newDayHeader}${linkLine}\n`;
}

// =======================
// Save back
// =======================
await app.vault.modify(sheetFile, sheet);

console.log("✅ LeetCode Sheet updated successfully");
%>
